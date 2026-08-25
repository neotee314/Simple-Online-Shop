package com.neotee.ecommercesystem.shopsystem.product.application.service;


import java.util.UUID;

public interface ReservationCheckServiceInterface {

    boolean isReservedInAnyBasket(UUID thingId);

    void deleteShoppingBasketParts();
}
