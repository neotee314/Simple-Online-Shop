package com.neotee.ecommercesystem.shopsystem.thing.application.service;

import java.util.UUID;


public interface InventoryServiceInterface {
    Boolean isInStock(UUID thingId);

    void deleteAllStockLevel();
}
