package com.neotee.ecommercesystem.restdtos;

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