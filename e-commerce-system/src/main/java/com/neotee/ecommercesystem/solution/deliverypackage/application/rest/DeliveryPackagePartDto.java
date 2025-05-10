package com.neotee.ecommercesystem.solution.deliverypackage.application.rest;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@Getter
public class DeliveryPackagePartDto {
    private UUID thingId;
    private int quantity;

    public DeliveryPackagePartDto(UUID thingId, int quantity) {
        this.thingId = thingId;
        this.quantity = quantity;
    }
}
