package com.neotee.ecommercesystem.domainprimitives;

import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class OrderPartId extends GenericId {
    protected OrderPartId() {
        super();
    }

    public OrderPartId(UUID id) {
        super(id);
    }

    public static OrderPartId of(UUID value) {
        return new OrderPartId(value);
    }

    public static OrderPartId newId() {
        return new OrderPartId(UUID.randomUUID());
    }

    public static OrderPartId of(String id) {
        if (id == null || id.isBlank()) {
            throw new DomainValidationException("OrderPartId", "id must not be null or blank");
        }

        try {
            return new OrderPartId(UUID.fromString(id));
        } catch (IllegalArgumentException ex) {
            throw new DomainValidationException("OrderPartId", "must be a valid UUID");
        }
    }
}