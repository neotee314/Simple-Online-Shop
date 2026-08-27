package com.neotee.ecommercesystem.shopsystem.deliverypackage.application.dto;

import java.util.List;
import java.util.UUID;

public record DeliveryPackageResponseDTO(
        UUID id,
        UUID orderId,
        UUID storageUnitId,
        List<DeliveryPackagePartResponseDTO> parts,
        int totalQuantity,
        int partCount
) {}