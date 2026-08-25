package com.neotee.ecommercesystem.domainprimitives;

import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class StockLevelId extends GenericId {
    protected StockLevelId() {
        super();
    }

    public StockLevelId(UUID id) {
        super(id);
    }

    public static StockLevelId of(UUID value) {
        return new StockLevelId(value);
    }

    public static StockLevelId newId() {
        return new StockLevelId(UUID.randomUUID());
    }

    public static StockLevelId of(String id) {
        if (id == null || id.isBlank())
            throw new DomainValidationException("StockLevelId", "id must not be null or blank");

        try {
            return new StockLevelId(UUID.fromString(id));
        } catch (IllegalArgumentException ex) {
            throw new DomainValidationException("StockLevelId", "must be a valid UUID");
        }
    }
}