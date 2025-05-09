package com.neotee.ecommercesystem.solution.thing.application;


import org.springframework.stereotype.Service;

import java.util.UUID;

public interface OrderedItemsServiceInterface {

    boolean isPartOfCompletedOrder(UUID thingId);

    void deleteOrderParts();
}
