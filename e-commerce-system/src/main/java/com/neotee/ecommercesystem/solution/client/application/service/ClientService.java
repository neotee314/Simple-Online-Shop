package com.neotee.ecommercesystem.solution.client.application.service;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.solution.client.domain.Client;
import com.neotee.ecommercesystem.solution.client.domain.ClientRepository;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ClientService {

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

    @Transactional
    public void addBasket(Email clientEmail, UUID shoppingBasketId) {
        Client client = getClient(clientEmail);
        client.addBasket(shoppingBasketId);
        clientRepository.save(client);
    }

    public void emptyBasket() {
        List<Client> clients = clientRepository.findAll();
        clients.forEach(Client::deleteBasket);
    }

    public List<Email> findAll() {
        return clientRepository.findAll().stream()
                .map(Client::getEmail)
                .toList();
    }
}
