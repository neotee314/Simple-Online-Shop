package com.neotee.ecommercesystem.shopsystem.storageunit.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockLevelDTO {
    private UUID stockLevelId;
    private UUID thingId;
    private Integer quantityInStock;
}
