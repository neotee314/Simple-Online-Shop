package com.neotee.ecommercesystem.domainprimitives;

import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class DeliveryContentId extends GenericId {
    protected DeliveryContentId() {
        super();
    }

    public DeliveryContentId(UUID id) {
        super(id);
    }

    public static DeliveryContentId of(UUID value) {
        return new DeliveryContentId(value);
    }

    public static DeliveryContentId newId() {
        return new DeliveryContentId(UUID.randomUUID());
    }

    public static DeliveryContentId of(String id) {
        if (id == null || id.isBlank()) {
            throw new DomainValidationException("DeliveryContentId", "id must not be null or blank");
        }

        try {
            return new DeliveryContentId(UUID.fromString(id));
        } catch (IllegalArgumentException ex) {
            throw new DomainValidationException("DeliveryContentId", "must be a valid UUID");
        }
    }
}
