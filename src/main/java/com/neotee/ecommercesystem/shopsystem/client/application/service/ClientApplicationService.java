package com.neotee.ecommercesystem.shopsystem.client.application.service;

import com.neotee.ecommercesystem.domainprimitives.ClientId;
import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.HomeAddress;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.exceptions.EntityNotFoundException;
import com.neotee.ecommercesystem.shopsystem.client.domain.Client;
import com.neotee.ecommercesystem.shopsystem.client.domain.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientApplicationService {

    private final ClientRepository clientRepository;


    public Client findByEmail(Email email) {
        return clientRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("ClientApplicationService", "Client mit dieser E-Mail nicht gefunden."));
    }

    public Client findById(ClientId clientId) {
        if (clientId == null) {
            throw new DomainValidationException("ClientApplicationService", "Client ID darf nicht null sein.");
        }

        return clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("ClientApplicationService", "Client nicht gefunden."));
    }

    public List<Client> findAll() {
        List<Client> clients = clientRepository.findAll();
        if (clients.isEmpty()) {
            throw new EntityNotFoundException("ClientApplicationService", "Keine Clients gefunden.");
        }
        return clients;
    }

    public boolean existsByEmail(Email email) {
        if (email == null) {
            throw new DomainValidationException("ClientApplicationService", "E-Mail darf nicht null sein.");
        }
        return clientRepository.findByEmail(email).isPresent();
    }

    
    public Client registerClient(String name, Email email, HomeAddress homeAddress) {
        if (existsByEmail(email)) {
            throw new DomainValidationException("ClientApplicationService", "Ein Client mit dieser E-Mail existiert bereits.");
        }

        Client client = Client.create(name, email, homeAddress);
        return clientRepository.save(client);
    }

    
    public Client updateClient(ClientId clientId, String name, Email email, HomeAddress homeAddress) {
        var client = findById(clientId);
        clientRepository.findByEmail(email)
                .ifPresent(existing -> {
                    if (!existing.getId().equals(clientId)) {
                        throw new DomainValidationException("ClientApplicationService", "Diese E-Mail wird bereits von einem anderen Client verwendet.");
                    }
                });

        client.updateName(name);
        client.setEmail(email);
        client.changeAddress(homeAddress);

        return clientRepository.save(client);
    }

    
    public Client updateClientName(ClientId clientId, String newName) {
        if (clientId == null) {
            throw new DomainValidationException("clientId", "Client ID darf nicht null sein.");
        }
        if (newName == null || newName.isBlank()) {
            throw new DomainValidationException("name", "Name darf nicht leer sein.");
        }

        Client client = findById(clientId);
        client.updateName(newName);
        return clientRepository.save(client);
    }

    
    public Client changeClientAddress(ClientId clientId, HomeAddress newAddress) {
        if (clientId == null) {
            throw new DomainValidationException("clientId", "Client ID darf nicht null sein.");
        }
        if (newAddress == null) {
            throw new DomainValidationException("homeAddress", "Adresse darf nicht null sein.");
        }

        Client client = findById(clientId);
        client.changeAddress(newAddress);
        return clientRepository.save(client);
    }

    
    public Client changeClientEmail(ClientId clientId, Email newEmail) {
        if (clientId == null) {
            throw new DomainValidationException("clientId", "Client ID darf nicht null sein.");
        }
        if (newEmail == null) {
            throw new DomainValidationException("email", "E-Mail darf nicht null sein.");
        }

        // Check if new email already exists (except for the same client)
        clientRepository.findByEmail(newEmail)
                .ifPresent(existing -> {
                    if (!existing.getId().equals(clientId)) {
                        throw new DomainValidationException("email", "Diese E-Mail wird bereits von einem anderen Client verwendet.");
                    }
                });

        Client client = findById(clientId);
        client.setEmail(newEmail);
        return clientRepository.save(client);
    }

    
    public void deleteClient(ClientId clientId) {
        if (clientId == null) {
            throw new DomainValidationException("clientId", "Client ID darf nicht null sein.");
        }

        Client client = findById(clientId);
        clientRepository.delete(client);
    }

    
    public void deleteAllClients() {
        clientRepository.deleteAll();
    }

}