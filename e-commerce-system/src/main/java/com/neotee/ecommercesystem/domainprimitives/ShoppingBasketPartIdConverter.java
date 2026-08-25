package com.neotee.ecommercesystem.domainprimitives;

import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ShoppingBasketPartIdConverter extends GenericIdConverter<ShoppingBasketPartId> {
    public ShoppingBasketPartIdConverter() {
        super(ShoppingBasketPartId::new);
    }
}
