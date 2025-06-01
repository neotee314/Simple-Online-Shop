package com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain;

import com.neotee.ecommercesystem.GenericIdConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ShoppingBasketPartIdConverter extends GenericIdConverter<ShoppingBasketPartId> {
    public ShoppingBasketPartIdConverter() {
        super(ShoppingBasketPartId::new);
    }
}
