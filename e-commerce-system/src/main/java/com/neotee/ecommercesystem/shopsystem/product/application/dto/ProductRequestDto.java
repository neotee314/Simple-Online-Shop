package com.neotee.ecommercesystem.shopsystem.product.application.dto;

import lombok.Data;

@Data
public class ProductRequestDto {
    private String name;
    private String description;
    private Float size;
    private Float purchasePrice;
    private Float salePrice;
}
