package com.neotee.ecommercesystem.solution.client.application.service;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.exception.EntityNotFoundException;
import com.neotee.ecommercesystem.exception.EntityIdNullException;
import com.neotee.ecommercesystem.solution.client.domain.Client;
import com.neotee.ecommercesystem.solution.client.domain.ClientRepository;
import com.neotee.ecommercesystem.solution.order.application.service.ClientOrderServiceInterface;
import com.neotee.ecommercesystem.solution.shoppingbasket.application.service.ClientBasketServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ClientService implements ClientBasketServiceInterface, ClientOrderServiceInterface {

    private final ClientRepository clientRepository;


    public Client getClient(Email email) {
        if (email == null) throw new ShopException("Email is null");
        return clientRepository.findByEmail(email);
    }

    public void deleteAllClients() {
        clientRepository.deleteAll();
    }

    public List<UUID> getOrderHistory(Email clientEmail) {
        Client client = getClient(clientEmail);
        if (client == null) {
            throw new ShopException("client does not exist");
        }
        return client.getOrderHistory();
    }

    public ZipCode findClientZipCode(Email clientEmail) {
        Client client = getClient(clientEmail);
        if (client == null) {
            throw new ShopException("client does not exist");
        }
        return client.findZipCode();
    }

    @Transactional
    public void addToOrderHistory(Email clientEmail, UUID orderId) {
        Client client = getClient(clientEmail);
        if (client == null) {
            throw new ShopException("client does not exist");
        }
        client.addToOrderHistory(orderId);
        clientRepository.save(client);
    }


    public void emptyAllBasket() {
        List<Client> clients = clientRepository.findAll();
        clients.forEach(Client::deleteBasket);
        clientRepository.saveAll(clients);
    }

    public List<Email> findAll() {
        return clientRepository.findAll().stream()
                .map(Client::getEmail)
                .toList();
    }

    public Client findById(UUID clientId) {
        return clientRepository.findById(clientId).orElse(null);
    }

    public Email findClientEmail(UUID clientId) {
        if (clientId == null) throw new EntityIdNullException();
        Client client = findById(clientId);
        if (client == null) throw new EntityNotFoundException();
        Email email = client.getEmail();
        if (email == null) throw new EntityNotFoundException();
        return client.getEmail();
    }
}
