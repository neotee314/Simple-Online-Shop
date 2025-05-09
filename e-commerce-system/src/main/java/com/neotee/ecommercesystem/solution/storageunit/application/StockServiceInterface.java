package com.neotee.ecommercesystem.solution.storageunit.application;

import java.util.UUID;

public interface StockServiceInterface {
    void removeFromStock(UUID thingId, int quantity);
    void addToStock(UUID thingId, int quantity);

}
