package com.neotee.ecommercesystem.shopsystem.storageunit.application.controller;

import com.neotee.ecommercesystem.shopsystem.storageunit.application.dto.AvailableStockDTO;
import com.neotee.ecommercesystem.shopsystem.storageunit.application.dto.StockResponseDTO;
import com.neotee.ecommercesystem.shopsystem.storageunit.application.dto.StorageUnitRequestDTO;
import com.neotee.ecommercesystem.shopsystem.storageunit.application.dto.StorageUnitResponseDTO;
import com.neotee.ecommercesystem.shopsystem.storageunit.application.service.StorageUnitApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/storageUnits")
@RequiredArgsConstructor
@Tag(name = "StorageUnit Management", description = "APIs for managing storage units and stock levels")
public class StorageUnitController {

    private final StorageUnitApplicationService storageUnitApplicationService;

    // === STORAGE UNIT CRUD ===

    @Operation(summary = "Get all storage units")
    @GetMapping
    public ResponseEntity<List<StorageUnitResponseDTO>> getAllStorageUnits() {
        return ResponseEntity.ok(storageUnitApplicationService.getAllStorageUnits());
    }

    @Operation(summary = "Get a storage unit by ID")
    @GetMapping("/{id}")
    public ResponseEntity<StorageUnitResponseDTO> getStorageUnitById(@PathVariable UUID id) {
        return ResponseEntity.ok(storageUnitApplicationService.getStorageUnitById(id));
    }

    @Operation(summary = "Create a new storage unit")
    @PostMapping
    public ResponseEntity<Void> createStorageUnit(@RequestBody StorageUnitRequestDTO dto) {
        storageUnitApplicationService.addNewStorageUnit(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete all storage units")
    @DeleteMapping
    public ResponseEntity<Void> deleteAllStorageUnits() {
        storageUnitApplicationService.deleteAllStorageUnits();
        return ResponseEntity.ok().build();
    }

    // === STOCK MANAGEMENT ===

    @Operation(summary = "Add stock to a storage unit")
    @PostMapping("/stock/add")
    public ResponseEntity<Void> addStock(@RequestBody StockResponseDTO dto) {
        storageUnitApplicationService.addToStock(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove stock from a storage unit")
    @PostMapping("/stock/remove")
    public ResponseEntity<Void> removeStock(@RequestBody StockResponseDTO dto) {
        storageUnitApplicationService.removeFromStock(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change stock level in a storage unit")
    @PostMapping("/stock/change")
    public ResponseEntity<Void> changeStock(@RequestBody StockResponseDTO dto) {
        storageUnitApplicationService.changeStockTo(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get available stock in a specific storage unit")
    @PostMapping("/stock/available")
    public ResponseEntity<Integer> getAvailableStock(@RequestBody AvailableStockDTO availableStockDTO) {
        int stock = storageUnitApplicationService.getAvailableStockInStorageUnit(availableStockDTO);
        return ResponseEntity.ok(stock);
    }

    @Operation(summary = "Get total available stock across all storage units")
    @GetMapping("/stock/available/total/{stockId}")
    public ResponseEntity<Integer> getTotalAvailableStock(@PathVariable UUID stockId) {
        int total = storageUnitApplicationService.getAvailableStock(stockId);
        return ResponseEntity.ok(total);
    }
}
