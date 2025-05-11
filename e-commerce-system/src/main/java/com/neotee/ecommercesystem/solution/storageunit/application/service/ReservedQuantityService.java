package com.neotee.ecommercesystem.solution.storageunit.application.service;

import java.util.UUID;

public interface ReservedQuantityService {
    int getTotalReservedInAllBaskets(UUID thingId);
    void removeFromReservedQuantity(UUID thingId, int removeFromReserved);
}
