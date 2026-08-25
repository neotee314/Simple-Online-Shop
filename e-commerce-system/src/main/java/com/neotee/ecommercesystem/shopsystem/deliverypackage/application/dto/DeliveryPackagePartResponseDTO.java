package com.neotee.ecommercesystem.shopsystem.deliverypackage.application.dto;

import com.neotee.ecommercesystem.domainprimitives.ProductId;

public record DeliveryPackagePartResponseDTO(
    ProductId productId,
    String productName,
    int quantity
) {}