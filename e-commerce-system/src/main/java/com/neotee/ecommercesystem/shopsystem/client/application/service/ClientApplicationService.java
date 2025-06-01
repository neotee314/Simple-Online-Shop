package com.neotee.ecommercesystem.shopsystem.client.application.service;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.exception.EntityNotFoundException;
import com.neotee.ecommercesystem.exception.ValueObjectNullOrEmptyException;
import com.neotee.ecommercesystem.shopsystem.client.application.dto.ClientDTO;
import com.neotee.ecommercesystem.shopsystem.client.application.mapper.ClientMapper;
import com.neotee.ecommercesystem.shopsystem.client.domain.Client;
import com.neotee.ecommercesystem.shopsystem.client.domain.ClientId;
import com.neotee.ecommercesystem.shopsystem.client.domain.ClientRepository;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto.ShoppingBasketDTO;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.mapper.ShoppingBasketMapper;
import com.neotee.ecommercesystem.usecases.ClientRegistrationUseCases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientApplicationService {
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final ClientRegistrationUseCases clientRegistrationUseCases;
    private final ShoppingBasketMapper shoppingBasketMapper;

    public ClientDTO findClientDTOByEmail(String emailaddress) {
        if (emailaddress == null || emailaddress.isEmpty()) throw new ValueObjectNullOrEmptyException();
        Email email = Email.of(emailaddress);
        Client client = clientRepository.findByEmail(email);
        if (client == null) throw new EntityNotFoundException();
        return clientMapper.toDto(client);
    }

    public ClientDTO findById(UUID id) {
        ClientId clientId = new ClientId(id);
        Client client = clientRepository.findById(clientId).orElseThrow(EntityNotFoundException::new);
        return clientMapper.toDto(client);
    }

    public List<ClientDTO> getAll() {
        List<ClientDTO> clientDTOS = new ArrayList<>();
        clientRepository.findAll().forEach(client -> clientDTOS.add(clientMapper.toDto(client)));
        return clientDTOS;
    }

    public void register(ClientDTO clientDTO) {
        if (clientDTO == null) throw new EntityNotFoundException();
        Client client = clientMapper.toEntity(clientDTO);
        clientRegistrationUseCases.register(client.getName(), client.getEmail(), client.getHomeAddress());
    }

    public void updateAddress(ClientDTO clientDTO) {
        if (clientDTO == null) throw new EntityNotFoundException();
        Client client = clientMapper.toEntity(clientDTO);
        clientRegistrationUseCases.changeAddress(client.getEmail(), client.getHomeAddress());
    }

    public ShoppingBasketDTO getBasketOf(UUID clientId) {
        if (clientId == null) throw new EntityNotFoundException();
        Client client = clientRepository.findById(new ClientId(clientId)).
                orElseThrow(EntityNotFoundException::new);
        ShoppingBasketDTO dto = shoppingBasketMapper.toDto(client.getShoppingBasket());
        return dto;
    }
}
