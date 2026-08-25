package com.neotee.ecommercesystem.anticorruption;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.HomeAddress;
import com.neotee.ecommercesystem.shopsystem.client.application.service.ClientApplicationService;
import com.neotee.ecommercesystem.usecases.ClientRegistrationUseCases;
import com.neotee.ecommercesystem.usecases.ClientType;
import com.neotee.ecommercesystem.domainprimitives.ClientTypeImp;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.HomeAddressType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyFantasticClientRegistrationService implements ClientRegistrationUseCases {

    private final ClientApplicationService clientApplicationService;

    @Override
    public void register(String name, EmailType email, HomeAddressType address) {
        var emailDomain = (Email) email;
        var addressDomain = (HomeAddress) address;
        clientApplicationService.registerClient(name, emailDomain, addressDomain);
    }

    @Override
    public void changeAddress(EmailType email, HomeAddressType address) {
        var emailDomain = (Email) email;
        var addressDomain = (HomeAddress) address;
        var client = clientApplicationService.findByEmail(emailDomain);
        clientApplicationService.changeClientAddress(client.getId(), addressDomain);
    }

    @Override
    public ClientType getClientData(EmailType clientEmail) {
        var emailDomain = (Email) clientEmail;
        var client = clientApplicationService.findByEmail(emailDomain);
        return new ClientTypeImp(
                client.getName(),
                client.getEmail(),
                client.getHomeAddress()
        );
    }

    @Override
    public void deleteAllClients() {
        clientApplicationService.deleteAllClients();
    }
}