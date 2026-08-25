package com.neotee.ecommercesystem.domainprimitives;

import com.neotee.ecommercesystem.domainprimitives.GenericId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class ProductId extends GenericId {

    protected ProductId() {
        super();
    }

    public ProductId(UUID id) {
        super(id);
    }

    public static ProductId of(UUID value) {
        return new ProductId(value);
    }

    public static ProductId newId() {
        return new ProductId(UUID.randomUUID());
    }

    public static ProductId of(String id) {
        if (id == null || id.isBlank()) {
            throw new DomainValidationException("ProductId", "id must not be null or blank");
        }

        try {
            return new ProductId(UUID.fromString(id));
        } catch (IllegalArgumentException ex) {
            throw new DomainValidationException("ProductId", "must be a valid UUID");
        }
    }
}