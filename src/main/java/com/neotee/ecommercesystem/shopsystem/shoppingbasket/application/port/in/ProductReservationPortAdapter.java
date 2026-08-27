package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.port.in;

import com.neotee.ecommercesystem.shopsystem.product.application.port.out.ProductReservationPort;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.model.ShoppingBasket;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.repository.ShoppingBasketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductReservationPortAdapter implements ProductReservationPort {

    private final ShoppingBasketRepository shoppingBasketRepository;

    @Override
    public boolean isReservedInAnyBasket(Product product) {
        return shoppingBasketRepository.findAll().stream().anyMatch(basket -> basket.contains(product));
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