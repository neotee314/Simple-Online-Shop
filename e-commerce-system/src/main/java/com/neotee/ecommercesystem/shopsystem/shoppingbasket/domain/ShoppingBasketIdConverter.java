package com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain;

import com.neotee.ecommercesystem.GenericIdConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ShoppingBasketIdConverter extends GenericIdConverter<ShoppingBasketId> {
    public ShoppingBasketIdConverter() {
        super(ShoppingBasketId::new);
    }
}
