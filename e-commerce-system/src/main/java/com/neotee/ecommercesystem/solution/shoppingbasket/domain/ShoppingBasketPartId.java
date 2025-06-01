package com.neotee.ecommercesystem.solution.shoppingbasket.domain;

import com.neotee.ecommercesystem.GenericId;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class ShoppingBasketPartId extends GenericId {
    protected ShoppingBasketPartId() { super(); }
    public ShoppingBasketPartId(UUID id) { super(id); }
}
