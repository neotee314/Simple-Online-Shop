package com.neotee.ecommercesystem.shopsystem.deliverypackage.application.controller;

import com.neotee.ecommercesystem.domainprimitives.OrderId;
import com.neotee.ecommercesystem.domainprimitives.StorageUnitId;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.dto.DeliveryPackageResponseDTO;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.mapper.DeliveryPackageMapper;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.service.DeliveryPackageApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/deliveryPackages")
@RequiredArgsConstructor
public class DeliveryPackageController {

    private final DeliveryPackageApplicationService deliveryPackageService;
    private final DeliveryPackageMapper deliveryPackageMapper;

    @Operation(summary = "Get delivery packages for an order")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<DeliveryPackageResponseDTO>> getDeliveryPackagesForOrder(@PathVariable UUID orderId) {

        var packages = deliveryPackageService.findByOrderId(OrderId.of(orderId));
        var response = packages.stream()
                .map(deliveryPackageMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get delivery package for order and storage unit")
    @GetMapping("/order/{orderId}/storageUnit/{storageUnitId}")
    public ResponseEntity<DeliveryPackageResponseDTO> getDeliveryPackageForOrderAndStorageUnit(@PathVariable UUID orderId, @PathVariable UUID storageUnitId) {
        var deliveryPackage = deliveryPackageService.findByOrderIdAndStorageUnitId(OrderId.of(orderId), StorageUnitId.of(storageUnitId));
        return ResponseEntity.ok(deliveryPackageMapper.toResponseDTO(deliveryPackage));
    }

    @Operation(summary = "Delete all delivery packages")
    @DeleteMapping
    public ResponseEntity<Void> deleteAllDeliveryPackages() {
        deliveryPackageService.deleteAllDeliveryPackages();
        return ResponseEntity.noContent().build();
    }
}