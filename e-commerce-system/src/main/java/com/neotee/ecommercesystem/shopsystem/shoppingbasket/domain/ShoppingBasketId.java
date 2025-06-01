package com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain;

import com.neotee.ecommercesystem.GenericId;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class ShoppingBasketId extends GenericId {
    protected ShoppingBasketId() { super(); }
    public ShoppingBasketId(UUID id) { super(id); }
}
