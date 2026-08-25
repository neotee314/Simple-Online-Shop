package com.neotee.ecommercesystem.shopsystem.client.application.service;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.HomeAddress;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.client.domain.Client;
import com.neotee.ecommercesystem.usecases.ClientRegistrationUseCases;
import com.neotee.ecommercesystem.usecases.ClientType;
import com.neotee.ecommercesystem.domainprimitives.ClientTypeImp;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.HomeAddressType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientRegistrationService implements ClientRegistrationUseCases {

    private final ClientApplicationService clientApplicationService;

    @Override
    @Transactional
    public void register(String name, EmailType email, HomeAddressType address) {
        if (email == null || address == null)
            throw new DomainValidationException("ClientRegistrationService", "Email or address cannot be null or blank");
        var emailDomain = (Email) email;
        var addressDomain = (HomeAddress) address;
        if (name == null || name.isBlank())
            throw new DomainValidationException("ClientRegistrationService", "Name cannot be null or blank");
        clientApplicationService.registerClient(name, emailDomain, addressDomain);
    }

    @Override
    @Transactional
    public void changeAddress(EmailType email, HomeAddressType address) {
        if (email == null || address == null)
            throw new DomainValidationException("ClientRegistrationService", "Email or address cannot be null or blank");
        var emailDomain = (Email) email;
        var addressDomain = (HomeAddress) address;
        var client = clientApplicationService.findByEmail(emailDomain);
        clientApplicationService.changeClientAddress(client.getId(), addressDomain);
    }

    @Override
    public ClientType getClientData(EmailType clientEmail) {
        if (clientEmail == null)
            throw new DomainValidationException("ClientRegistrationService", "Email  cannot be null or blank");
        var emailDomain = (Email) clientEmail;
        var client = clientApplicationService.findByEmail(emailDomain);
        return new ClientTypeImp(
                client.getName(),
                client.getEmail(),
                client.getHomeAddress()
        );
    }

    @Override
    @Transactional
    public void deleteAllClients() {
        clientApplicationService.deleteAllClients();
    }
}