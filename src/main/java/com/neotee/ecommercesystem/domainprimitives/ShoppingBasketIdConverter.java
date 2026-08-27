package com.neotee.ecommercesystem.domainprimitives;

import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ShoppingBasketIdConverter extends GenericIdConverter<ShoppingBasketId> {
    public ShoppingBasketIdConverter() {
        super(ShoppingBasketId::new);
    }
}
