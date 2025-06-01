package com.neotee.ecommercesystem.shopsystem.thing.application.service;


import java.util.UUID;

public interface ReservationCheckServiceInterface {

    boolean isReservedInAnyBasket(UUID thingId);

    void deleteShoppingBasketParts();
}
