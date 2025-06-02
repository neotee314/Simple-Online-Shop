package com.neotee.ecommercesystem.shopsystem.storageunit.application.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AvailableStockDTO {
    private UUID storageUnitId;
    private UUID thingId;
}
