package com.neotee.ecommercesystem.shopsystem.product.application.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductResponseDto {
    private UUID id;
    private String name;
    private String description;
    private Float size;
    private Float purchasePrice;
    private Float salePrice;
}
