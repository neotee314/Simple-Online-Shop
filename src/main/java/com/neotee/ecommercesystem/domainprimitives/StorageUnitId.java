package com.neotee.ecommercesystem.domainprimitives;

import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class StorageUnitId extends GenericId {
    protected StorageUnitId() {
        super();
    }

    public StorageUnitId(UUID id) {
        super(id);
    }

    public static StorageUnitId of(UUID value) {
        return new StorageUnitId(value);
    }

    public static StorageUnitId newId() {
        return new StorageUnitId(UUID.randomUUID());
    }

    public static StorageUnitId of(String id) {
        if (id == null || id.isBlank())
            throw new DomainValidationException("StorageUnitId", "id must not be null or blank");

        try {
            return new StorageUnitId(UUID.fromString(id));
        } catch (IllegalArgumentException ex) {
            throw new DomainValidationException("StorageUnitId", "must be a valid UUID");
        }
    }
}