package com.neotee.ecommercesystem.solution.shoppingbasket.application.dto;


import lombok.Data;

import java.util.UUID;

@Data
public class AddThingRequest {
    private UUID thingId;
    private String name;
    private int quantity;
    private double price;
}
