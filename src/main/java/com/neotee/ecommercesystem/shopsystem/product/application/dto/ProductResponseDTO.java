package com.neotee.ecommercesystem.shopsystem.product.application.dto;


import java.util.UUID;

public record ProductResponseDTO(
    UUID id,
    String name,
    String description,
    Float size,
    Float purchasePrice,
    Float salePrice,
    Integer stockQuantity
) {}