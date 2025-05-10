package com.neotee.ecommercesystem.solution.storageunit.application.service;

import java.util.UUID;

public interface StockServiceInterface {
    void removeFromStock(UUID thingId, int quantity);
    void addToStock(UUID thingId, int quantity);

    boolean existsById(UUID thingId);

    void changeStockTo(UUID thingId, int newTotalQuantity);
}
