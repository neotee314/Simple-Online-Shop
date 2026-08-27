package com.neotee.ecommercesystem.domainprimitives;

import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class DeliveryPackagePartId extends GenericId {
    protected DeliveryPackagePartId() {
        super();
    }

    public DeliveryPackagePartId(UUID id) {
        super(id);
    }

    public static DeliveryPackagePartId of(UUID value) {
        return new DeliveryPackagePartId(value);
    }

    public static DeliveryPackagePartId newId() {
        return new DeliveryPackagePartId(UUID.randomUUID());
    }

    public static DeliveryPackagePartId of(String id) {
        if (id == null || id.isBlank())
            throw new DomainValidationException("DeliveryPackagePartId", "id must not be null or blank");

        try {
            return new DeliveryPackagePartId(UUID.fromString(id));
        } catch (IllegalArgumentException ex) {
            throw new DomainValidationException("DeliveryPackagePartId", "must be a valid UUID");
        }
    }
}