package com.neotee.ecommercesystem.shopsystem.delivery.application.api;

import com.neotee.ecommercesystem.domainprimitives.DeliveryId;
import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.shopsystem.delivery.application.dto.DeliveryPackageResponseDTO;
import com.neotee.ecommercesystem.shopsystem.delivery.application.dto.DeliveryResponseDTO;
import com.neotee.ecommercesystem.shopsystem.delivery.application.dto.UpdateDeliveryPackageStatusRequestDTO;
import com.neotee.ecommercesystem.shopsystem.delivery.application.mapper.DeliveryMapper;
import com.neotee.ecommercesystem.shopsystem.delivery.application.service.DeliveryApplicationService;
import com.neotee.ecommercesystem.usecases.DeliveryUseCases;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryUseCases deliveryUseCases;
    private final DeliveryApplicationService deliveryApplicationService;
    private final DeliveryMapper deliveryMapper;

    @Operation(summary = "Get delivery by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Delivery found"),
        @ApiResponse(responseCode = "404", description = "Delivery not found")
    })
    @GetMapping("/{deliveryId}")
    public ResponseEntity<DeliveryResponseDTO> getDeliveryById(@PathVariable UUID deliveryId) {
        var delivery = deliveryApplicationService.findById(DeliveryId.of(deliveryId));
        return ResponseEntity.ok(deliveryMapper.toResponseDTO(delivery));
    }

    @Operation(summary = "Get all delivery packages for a delivery")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Packages found"),
        @ApiResponse(responseCode = "404", description = "Delivery not found")
    })
    @GetMapping("/{deliveryId}/packages")
    public ResponseEntity<List<DeliveryPackageResponseDTO>> getDeliveryPackages(@PathVariable UUID deliveryId) {
        var packageIds = deliveryUseCases.getDeliveryPackages(deliveryId);
        var packages = deliveryApplicationService.getDeliveryPackagesDetails(deliveryId);
        return ResponseEntity.ok(packages.stream()
                .map(deliveryMapper::toPackageResponseDTO)
                .toList());
    }

    @Operation(summary = "Get delivery package status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status found"),
        @ApiResponse(responseCode = "404", description = "Package not found")
    })
    @GetMapping("/packages/{packageId}/status")
    public ResponseEntity<String> getDeliveryPackageStatus(@PathVariable UUID packageId) {
        var status = deliveryUseCases.getDeliveryPackageStatus(packageId);
        return ResponseEntity.ok(status);
    }

    @Operation(summary = "Update delivery package status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status value"),
        @ApiResponse(responseCode = "404", description = "Package not found")
    })
    @PatchMapping("/packages/{packageId}/status")
    public ResponseEntity<Void> updateDeliveryPackageStatus(
            @PathVariable UUID packageId,
            @Valid @RequestBody UpdateDeliveryPackageStatusRequestDTO request) {
        if (request==null) return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        deliveryUseCases.updateDeliveryPackageStatus(packageId, request.status());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get delivery history for a client")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "History found"),
        @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping("/history")
    public ResponseEntity<List<UUID>> getDeliveryHistory(@RequestParam String email) {
        var history = deliveryUseCases.getDeliveryHistory(Email.of(email));
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "Delete all deliveries")
    @ApiResponse(responseCode = "204", description = "All deliveries deleted")
    @DeleteMapping
    public ResponseEntity<Void> deleteAllDeliveries() {
        deliveryUseCases.deleteAllDeliveries();
        return ResponseEntity.noContent().build();
    }
}