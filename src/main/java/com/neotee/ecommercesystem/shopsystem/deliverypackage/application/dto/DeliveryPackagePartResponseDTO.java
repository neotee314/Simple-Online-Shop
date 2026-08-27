package com.neotee.ecommercesystem.shopsystem.deliverypackage.application.dto;

import java.util.UUID;

public record DeliveryPackagePartResponseDTO(
        UUID productId,
        String productName,
        int quantity
) {}