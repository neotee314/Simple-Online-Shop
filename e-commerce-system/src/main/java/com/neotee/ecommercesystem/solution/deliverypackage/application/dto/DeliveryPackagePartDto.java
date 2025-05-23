package com.neotee.ecommercesystem.solution.deliverypackage.application.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class DeliveryPackagePartDto {
    private UUID thingId;
    private int quantity;
}
