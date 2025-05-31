package com.neotee.ecommercesystem.solution.client.application.service;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.exception.EntityNotFoundException;
import com.neotee.ecommercesystem.exception.ValueObjectNullOrEmptyException;
import com.neotee.ecommercesystem.solution.client.application.dto.ClientDTO;
import com.neotee.ecommercesystem.solution.client.application.mapper.ClientMapper;
import com.neotee.ecommercesystem.solution.client.domain.Client;
import com.neotee.ecommercesystem.solution.client.domain.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientApplicationService {
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public ClientDTO findClientDTOByEmail(String emailaddress) {
        if (emailaddress == null || emailaddress.isEmpty()) throw new ValueObjectNullOrEmptyException();
        Email email = Email.of(emailaddress);
        Client client = clientRepository.findByEmail(email);
        if (client == null) throw new EntityNotFoundException();
        return clientMapper.toDto(client);
    }
}
