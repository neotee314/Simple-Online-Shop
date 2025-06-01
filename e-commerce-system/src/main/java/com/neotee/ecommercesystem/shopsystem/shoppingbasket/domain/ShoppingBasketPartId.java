package com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain;

import com.neotee.ecommercesystem.GenericId;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class ShoppingBasketPartId extends GenericId {
    protected ShoppingBasketPartId() { super(); }
    public ShoppingBasketPartId(UUID id) { super(id); }
}
