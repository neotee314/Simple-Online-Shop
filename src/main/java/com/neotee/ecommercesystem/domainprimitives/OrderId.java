package com.neotee.ecommercesystem.domainprimitives;

import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class OrderId extends GenericId {
    protected OrderId() {
        super();
    }

    public OrderId(UUID id) {
        super(id);
    }

    public static OrderId of(UUID value) {
        return new OrderId(value);
    }

    public static OrderId newId() {
        return new OrderId(UUID.randomUUID());
    }

    public static OrderId of(String id) {
        if (id == null || id.isBlank()) {
            throw new DomainValidationException("OrderId", "id must not be null or blank");
        }

        try {
            return new OrderId(UUID.fromString(id));
        } catch (IllegalArgumentException ex) {
            throw new DomainValidationException("OrderId", "must be a valid UUID");
        }
    }
}