package com.neotee.ecommercesystem.shopsystem.thing.application.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ResponseThingDTO {
    private UUID id;
    private String name;
    private String description;
    private Float size;
    private Float purchasePrice;
    private Float salePrice;
}
