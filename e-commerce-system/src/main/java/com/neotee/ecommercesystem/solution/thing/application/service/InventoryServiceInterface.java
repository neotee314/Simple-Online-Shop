package com.neotee.ecommercesystem.solution.thing.application.service;

import com.neotee.ecommercesystem.solution.thing.domain.ThingId;

import java.util.UUID;


public interface InventoryServiceInterface {
    Boolean isInStock(UUID thingId);

    void deleteAllStockLevel();
}
