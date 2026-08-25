package com.neotee.ecommercesystem.shopsystem.storageunit.application.dto;

import com.neotee.ecommercesystem.domainprimitives.ProductId;

public record StockLevelResponseDTO(
    ProductId productId,
    String productName,
    Integer quantityInStock
) {}