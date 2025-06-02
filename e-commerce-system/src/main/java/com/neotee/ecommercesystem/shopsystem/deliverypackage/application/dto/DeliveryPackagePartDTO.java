package com.neotee.ecommercesystem.shopsystem.deliverypackage.application.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class DeliveryPackagePartDTO {
    private UUID thingId;
    private int quantity;
}
