package com.neotee.ecommercesystem.solution.client.domain;

import com.neotee.ecommercesystem.GenericIdConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ClientIdConverter extends GenericIdConverter<ClientId> {
    public ClientIdConverter() {
        super(ClientId::new);
    }
}
