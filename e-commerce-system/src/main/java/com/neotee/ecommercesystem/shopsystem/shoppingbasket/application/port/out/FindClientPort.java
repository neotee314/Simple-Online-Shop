package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.port.out;

import com.neotee.ecommercesystem.domainprimitives.ClientId;
import com.neotee.ecommercesystem.shopsystem.client.domain.Client;

public interface FindClientPort {
    Client findById(ClientId clientId);
}