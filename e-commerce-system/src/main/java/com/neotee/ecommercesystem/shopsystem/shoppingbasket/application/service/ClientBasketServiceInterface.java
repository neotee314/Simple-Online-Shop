package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.service;

import com.neotee.ecommercesystem.domainprimitives.Email;

import java.util.UUID;

public interface ClientBasketServiceInterface {
    void emptyAllBasket();

    Email findClientEmail(UUID clientId);
}
