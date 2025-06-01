package com.neotee.ecommercesystem.solution.shoppingbasket.domain;

import com.neotee.ecommercesystem.GenericIdConverter;
import com.neotee.ecommercesystem.solution.client.domain.ClientId;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ShoppingBasketIdConverter extends GenericIdConverter<ShoppingBasketId> {
    public ShoppingBasketIdConverter() {
        super(ShoppingBasketId::new);
    }
}
