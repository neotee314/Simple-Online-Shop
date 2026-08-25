package com.neotee.ecommercesystem.domainprimitives;

import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class ClientId extends GenericId {
    protected ClientId() {
        super();
    }

    public ClientId(UUID id) {
        super(id);
    }

    public static ClientId of(UUID value) {
        return new ClientId(value);
    }

    public static ClientId newId() {
        return new ClientId(UUID.randomUUID());
    }

    public static ClientId of(String id) {
        if (id == null || id.isBlank()) {
            throw new DomainValidationException("ClientId", "id must not be null or blank");
        }

        try {
            return new ClientId(UUID.fromString(id));
        } catch (IllegalArgumentException ex) {
            throw new DomainValidationException("ClientId", "must be a valid UUID");
        }
    }

}
