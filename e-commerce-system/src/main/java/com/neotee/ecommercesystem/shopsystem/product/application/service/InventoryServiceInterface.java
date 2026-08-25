package com.neotee.ecommercesystem.shopsystem.product.application.service;

import java.util.UUID;


public interface InventoryServiceInterface {
    Boolean isInStock(UUID thingId);

    void deleteAllStockLevel();
}
