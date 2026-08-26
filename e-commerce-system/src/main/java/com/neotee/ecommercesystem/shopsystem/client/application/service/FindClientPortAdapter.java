package com.neotee.ecommercesystem.shopsystem.client.application.service;

import com.neotee.ecommercesystem.domainprimitives.ClientId;
import com.neotee.ecommercesystem.shopsystem.client.domain.Client;
import com.neotee.ecommercesystem.shopsystem.client.domain.ClientRepository;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.port.out.FindClientPort;
import com.neotee.ecommercesystem.exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FindClientPortAdapter implements FindClientPort {

    private final ClientRepository clientRepository;

    @Override
    public Client findById(ClientId clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("FindClientPortAdapter", "Client not found with id: " + clientId));
    }
}