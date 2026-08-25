package com.neotee.ecommercesystem.domainprimitives;

import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class ShoppingBasketId extends GenericId {
    protected ShoppingBasketId() { super(); }
    public ShoppingBasketId(UUID id) { super(id); }

    public static ShoppingBasketId of(UUID value) {
        return new ShoppingBasketId(value);
    }

    public static ShoppingBasketId newId() {
        return new ShoppingBasketId(UUID.randomUUID());
    }

    public static ShoppingBasketId of(String id) {
        if (id == null || id.isBlank())
            throw new DomainValidationException("ShoppingBasketId", "id must not be null or blank");
        try {
            return new ShoppingBasketId(UUID.fromString(id));
        } catch (IllegalArgumentException ex) {
            throw new DomainValidationException("ShoppingBasketId", "must be a valid UUID");
        }
    }
}