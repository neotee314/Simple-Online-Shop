package com.neotee.ecommercesystem.solution.thing.application.service;


import java.util.UUID;

public interface ReservationCheckServiceInterface {

    boolean isReservedInAnyBasket(UUID thingId);

    void deleteShoppingBasketParts();
}
