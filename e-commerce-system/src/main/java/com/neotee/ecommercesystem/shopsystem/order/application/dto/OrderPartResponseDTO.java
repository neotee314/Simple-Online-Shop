package com.neotee.ecommercesystem.shopsystem.order.application.dto;

import com.neotee.ecommercesystem.domainprimitives.ProductId;

public record OrderPartResponseDTO(
    ProductId productId,
    String productName,
    int quantity
) {}