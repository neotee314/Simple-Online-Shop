package com.neotee.ecommercesystem.solution.thing.application;


import java.util.UUID;


public interface ReservationServiceInterface {

    boolean isReservedInBasket(UUID thingId);
}
