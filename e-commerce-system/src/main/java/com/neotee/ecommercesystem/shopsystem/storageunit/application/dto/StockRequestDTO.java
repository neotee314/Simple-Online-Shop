package com.neotee.ecommercesystem.shopsystem.storageunit.application.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class StockRequestDTO {
    private UUID storageUnitId;
    private UUID thingId;
    private Integer quantity;
}
