package com.neotee.ecommercesystem.domainprimitives;

import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ClientIdConverter extends GenericIdConverter<ClientId> {
    public ClientIdConverter() {
        super(ClientId::new);
    }
}
