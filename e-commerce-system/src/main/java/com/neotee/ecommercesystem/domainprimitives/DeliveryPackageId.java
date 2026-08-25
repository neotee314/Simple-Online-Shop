package com.neotee.ecommercesystem.domainprimitives;

import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class DeliveryPackageId extends GenericId {
    protected DeliveryPackageId() {
        super();
    }

    public DeliveryPackageId(UUID id) {
        super(id);
    }

    public static DeliveryPackageId of(UUID value) {
        return new DeliveryPackageId(value);
    }

    public static DeliveryPackageId newId() {
        return new DeliveryPackageId(UUID.randomUUID());
    }

    public static DeliveryPackageId of(String id) {
        if (id == null || id.isBlank())
            throw new DomainValidationException("DeliveryPackageId", "id must not be null or blank");

        try {
            return new DeliveryPackageId(UUID.fromString(id));
        } catch (IllegalArgumentException ex) {
            throw new DomainValidationException("DeliveryPackageId", "must be a valid UUID");
        }
    }
}