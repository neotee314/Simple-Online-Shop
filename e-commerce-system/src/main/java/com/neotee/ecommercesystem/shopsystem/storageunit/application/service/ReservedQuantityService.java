package com.neotee.ecommercesystem.shopsystem.storageunit.application.service;

import java.util.UUID;

public interface ReservedQuantityService {
    Integer getTotalReservedInAllBaskets(UUID thingId);
    void removeFromReservedQuantity(UUID thingId, Integer removeFromReserved);
}
