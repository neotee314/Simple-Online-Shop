package com.neotee.ecommercesystem.solution.thing.application.service;


import java.util.UUID;

@FunctionalInterface
public interface ReservationCatalogServiceInterface {

    boolean isReservedInBasket(UUID thingId);
}
