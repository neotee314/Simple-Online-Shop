package com.neotee.ecommercesystem.shopsystem.storageunit.application.api;

import com.neotee.ecommercesystem.domainprimitives.HomeAddress;
import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.domainprimitives.StorageUnitId;
import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.shopsystem.storageunit.application.dto.StorageUnitRequestDTO;
import com.neotee.ecommercesystem.shopsystem.storageunit.application.dto.StorageUnitResponseDTO;
import com.neotee.ecommercesystem.shopsystem.storageunit.application.mapper.StorageUnitMapper;
import com.neotee.ecommercesystem.shopsystem.storageunit.application.service.StorageUnitApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/storageUnits")
@RequiredArgsConstructor
public class StorageUnitController {

    private final StorageUnitApplicationService storageUnitService;
    private final StorageUnitMapper storageUnitMapper;

    @Operation(summary = "Get all storage units")
    @GetMapping
    public ResponseEntity<List<StorageUnitResponseDTO>> getAllStorageUnits() {
        var storageUnits = storageUnitService.findAll();
        var response = storageUnits.stream()
                .map(storageUnitMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get a storage unit by ID")
    @GetMapping("/{id}")
    public ResponseEntity<StorageUnitResponseDTO> getStorageUnitById(@PathVariable UUID id) {
        var storageUnit = storageUnitService.findById(StorageUnitId.of(id));
        return ResponseEntity.ok(storageUnitMapper.toResponseDTO(storageUnit));
    }

    @Operation(summary = "Create a new storage unit")
    @PostMapping
    public ResponseEntity<StorageUnitResponseDTO> createStorageUnit(@Valid @RequestBody StorageUnitRequestDTO request) {
        var address = (HomeAddress) HomeAddress.of(request.street(), request.city(), ZipCode.of(request.zipCode()));
        var storageUnit = storageUnitService.createStorageUnit(address, request.name());
        return new ResponseEntity<>(storageUnitMapper.toResponseDTO(storageUnit), HttpStatus.CREATED);
    }

    @Operation(summary = "Add stock to a storage unit")
    @PostMapping("/{storageUnitId}/stocks/{productId}/add")
    public ResponseEntity<Void> addStock(@PathVariable UUID storageUnitId, @PathVariable UUID productId, @RequestParam Integer quantity) {
        storageUnitService.addToStock(StorageUnitId.of(storageUnitId), ProductId.of(productId), quantity);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Remove stock from a storage unit")
    @PostMapping("/{storageUnitId}/stocks/{productId}/remove")
    public ResponseEntity<Void> removeStock(
            @PathVariable UUID storageUnitId,
            @PathVariable UUID productId,
            @RequestParam int quantity) {
        storageUnitService.removeFromStock(
                StorageUnitId.of(storageUnitId),
                ProductId.of(productId),
                quantity
        );
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Change stock level in a storage unit")
    @PutMapping("/{storageUnitId}/stocks/{productId}")
    public ResponseEntity<Void> changeStock(
            @PathVariable UUID storageUnitId,
            @PathVariable UUID productId,
            @RequestParam Integer newQuantity) {
        storageUnitService.changeStockTo(
                StorageUnitId.of(storageUnitId),
                ProductId.of(productId),
                newQuantity
        );
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get available stock in a specific storage unit")
    @GetMapping("/{storageUnitId}/stocks/{productId}")
    public ResponseEntity<Integer> getAvailableStockInStorageUnit(
            @PathVariable UUID storageUnitId,
            @PathVariable UUID productId) {
        var stock = storageUnitService.getAvailableStockInStorageUnit(
                StorageUnitId.of(storageUnitId),
                ProductId.of(productId)
        );
        return ResponseEntity.ok(stock);
    }

    @Operation(summary = "Get total available stock across all storage units")
    @GetMapping("/stocks/total/{productId}")
    public ResponseEntity<Integer> getTotalAvailableStock(@PathVariable UUID productId) {
        var total = storageUnitService.getAvailableStock(ProductId.of(productId));
        return ResponseEntity.ok(total);
    }

    @Operation(summary = "Delete all storage units")
    @DeleteMapping
    public ResponseEntity<Void> deleteAllStorageUnits() {
        storageUnitService.deleteAllStorageUnits();
        return ResponseEntity.noContent().build();
    }
}