package com.neotee.ecommercesystem.shopsystem.client.application.service;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.HomeAddress;
import com.neotee.ecommercesystem.exception.EntityNotFoundException;
import com.neotee.ecommercesystem.shopsystem.client.domain.Client;
import com.neotee.ecommercesystem.shopsystem.client.domain.ClientRepository;
import com.neotee.ecommercesystem.shopsystem.client.domain.ClientTypeImp;
import com.neotee.ecommercesystem.usecases.ClientRegistrationUseCases;
import com.neotee.ecommercesystem.usecases.ClientType;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.HomeAddressType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientRegistrationService implements ClientRegistrationUseCases {

    private final ClientRepository clientRepository;

    @Override
    @Transactional
    public void register(String name, EmailType email, HomeAddressType address) {
        if (clientRepository.findByEmail((Email) email) != null) {
            throw new ShopException("Client already exists");
        }

        Client client = new Client(name, (Email) email, (HomeAddress) address);
        clientRepository.save(client);
    }

    @Override
    public void changeAddress(EmailType email, HomeAddressType address) {
       Client client = clientRepository.findByEmail((Email) email);
        if (client == null) throw new EntityNotFoundException();

        client.changeAddress((HomeAddress) address);
        clientRepository.save(client);
    }

    @Override
    public ClientType getClientData(EmailType clientEmail) {
        Client client = clientRepository.findByEmail((Email) clientEmail);
        if (client == null) throw new EntityNotFoundException();

        return new ClientTypeImp(
                client.getName(),
                client.getEmail(),
                client.getHomeAddress()
        );
    }

    @Override
    public void deleteAllClients() {
        clientRepository.deleteAll();
    }
}
