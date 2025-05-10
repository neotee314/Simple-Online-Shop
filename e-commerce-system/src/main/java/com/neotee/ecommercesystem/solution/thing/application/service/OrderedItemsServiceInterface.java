package com.neotee.ecommercesystem.solution.thing.application.service;


import java.util.UUID;

public interface OrderedItemsServiceInterface {

    boolean isPartOfCompletedOrder(UUID thingId);

    void deleteOrderParts();
}
