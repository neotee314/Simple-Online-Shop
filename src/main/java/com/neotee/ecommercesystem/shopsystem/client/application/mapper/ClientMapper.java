package com.neotee.ecommercesystem.shopsystem.client.application.mapper;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.HomeAddress;
import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.shopsystem.client.application.dto.ClientRequestDto;
import com.neotee.ecommercesystem.shopsystem.client.application.dto.ClientResponseDto;
import com.neotee.ecommercesystem.shopsystem.client.domain.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public Client toEntity(ClientRequestDto dto) {
        var email = Email.of(dto.email());

        var zipCode = (ZipCode) ZipCode.of(dto.zipCode());
        var homeAddress = (HomeAddress) HomeAddress.of(
                dto.street(),
                dto.city(),
                zipCode
        );

        return Client.create(dto.name(), email, homeAddress);
    }

    public ClientResponseDto toDto(Client client) {
        if (client == null) {
            return null;
        }

        return new ClientResponseDto(
                client.getId() != null ? client.getId().getId() : null,
                client.getName(),
                client.getEmail() != null ? client.getEmail().getEmailAddress() : null,
                client.getHomeAddress() != null ? client.getHomeAddress().getStreet() : null,
                client.getHomeAddress() != null ? client.getHomeAddress().getCity() : null,
                client.getHomeAddress() != null && client.getHomeAddress().getZipCode() != null
                        ? client.getHomeAddress().getZipCode().getZipCode()
                        : null
        );
    }
}