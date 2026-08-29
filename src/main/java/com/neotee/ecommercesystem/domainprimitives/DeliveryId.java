package com.neotee.ecommercesystem.domainprimitives;

import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class DeliveryId extends GenericId {
    protected DeliveryId() {
        super();
    }

    public DeliveryId(UUID id) {
        super(id);
    }

    public static DeliveryId of(UUID value) {
        return new DeliveryId(value);
    }

    public static DeliveryId newId() {
        return new DeliveryId(UUID.randomUUID());
    }

    public static DeliveryId of(String id) {
        if (id == null || id.isBlank()) {
            throw new DomainValidationException("ClientId", "id must not be null or blank");
        }

        try {
            return new DeliveryId(UUID.fromString(id));
        } catch (IllegalArgumentException ex) {
            throw new DomainValidationException("DeliveryId", "must be a valid UUID");
        }
    }
}
