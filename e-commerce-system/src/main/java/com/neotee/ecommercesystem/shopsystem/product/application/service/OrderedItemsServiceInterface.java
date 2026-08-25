package com.neotee.ecommercesystem.shopsystem.product.application.service;


import java.util.UUID;

public interface OrderedItemsServiceInterface {

    boolean isPartOfCompletedOrder(UUID thingId);

    void deleteOrderParts();
}
