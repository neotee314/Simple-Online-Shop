package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.service;

import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.shopsystem.product.application.port.out.ProductReservationPort;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.ShoppingBasket;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.ShoppingBasketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductReservationPortAdapter implements ProductReservationPort {

    private final ShoppingBasketRepository shoppingBasketRepository;

    @Override
    public boolean isReservedInAnyBasket(ProductId productId) {
        if (productId == null) {
            return false;
        }

        return shoppingBasketRepository.findAll().stream()
                .flatMap(basket -> basket.getParts().stream())
                .anyMatch(part -> part.getProduct().getId().equals(productId));
    }

    @Override
    public void deleteShoppingBasketParts() {
        var allBaskets = shoppingBasketRepository.findAll();
        for (ShoppingBasket basket : allBaskets) {
            basket.clear();
            shoppingBasketRepository.save(basket);
        }
    }
}