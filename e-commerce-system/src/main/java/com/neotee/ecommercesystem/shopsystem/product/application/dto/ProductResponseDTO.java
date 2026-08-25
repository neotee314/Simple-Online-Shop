package com.neotee.ecommercesystem.shopsystem.product.application.dto;

import com.neotee.ecommercesystem.domainprimitives.ProductId;

public record ProductResponseDTO(
    ProductId id,
    String name,
    String description,
    Float size,
    Float purchasePrice,
    Float salePrice,
    Integer stockQuantity
) {}