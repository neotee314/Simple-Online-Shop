package com.neotee.ecommercesystem.shopsystem.deliverypackage.application.dto;

import com.neotee.ecommercesystem.domainprimitives.DeliveryPackageId;
import com.neotee.ecommercesystem.domainprimitives.OrderId;
import com.neotee.ecommercesystem.domainprimitives.StorageUnitId;

import java.util.List;

public record DeliveryPackageResponseDTO(
    DeliveryPackageId id,
    OrderId orderId,
    StorageUnitId storageUnitId,
    List<DeliveryPackagePartResponseDTO> parts,
    int totalQuantity,
    int partCount
) {}