package com.neotee.ecommercesystem.solution.shoppingbasket.application.service;

import com.neotee.ecommercesystem.solution.client.domain.Client;

import java.util.UUID;

public interface ClientBasketServiceInterface {
    void emptyAllBasket();

    Client findById(UUID clientId);
}
