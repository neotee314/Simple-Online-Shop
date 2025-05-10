package com.neotee.ecommercesystem.solution.thing.application;


import java.util.UUID;


public interface ReservationCatalogServiceInterface {

    boolean isReservedInBasket(UUID thingId);
}
