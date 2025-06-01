package com.neotee.ecommercesystem.solution.shoppingbasket.domain;

import com.neotee.ecommercesystem.GenericIdConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ShoppingBasketPartIdConverter extends GenericIdConverter<ShoppingBasketPartId> {
    public ShoppingBasketPartIdConverter() {
        super(ShoppingBasketPartId::new);
    }
}
