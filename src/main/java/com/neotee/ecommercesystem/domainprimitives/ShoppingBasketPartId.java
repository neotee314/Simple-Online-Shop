package com.neotee.ecommercesystem.domainprimitives;

import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class ShoppingBasketPartId extends GenericId {
    protected ShoppingBasketPartId() { super(); }
    public ShoppingBasketPartId(UUID id) { super(id); }

    public static ShoppingBasketPartId of(UUID value) {
        return new ShoppingBasketPartId(value);
    }

    public static ShoppingBasketPartId newId() {
        return new ShoppingBasketPartId(UUID.randomUUID());
    }

    public static ShoppingBasketPartId of(String id) {
        if (id == null || id.isBlank())
            throw new DomainValidationException("ShoppingBasketPartId", "id must not be null or blank");
        try {
            return new ShoppingBasketPartId(UUID.fromString(id));
        } catch (IllegalArgumentException ex) {
            throw new DomainValidationException("ShoppingBasketPartId", "must be a valid UUID");
        }
    }
}