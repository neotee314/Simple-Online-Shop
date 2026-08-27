package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.listener;

import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.model.ShoppingBasket;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.repository.ShoppingBasketRepository;
import com.neotee.ecommercesystem.events.StockChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StockEventListener {

    private final ShoppingBasketRepository basketRepository;

    @EventListener
    @Transactional
    public void onStockChanged(StockChangedEvent event) {
        var productId = event.productId();
        var newTotalStock = event.newQuantity();
        var allBaskets = basketRepository.findAll();

        int totalReserved = 0;
        for (var basket : allBaskets) {
            totalReserved += basket.getReservedQuantityForProduct(productId);
        }

        if (newTotalStock < totalReserved) {
            int toRemove = totalReserved - newTotalStock;
            reduceStockRandomly(allBaskets, productId, toRemove);
        }
    }

    private void reduceStockRandomly(List<ShoppingBasket> baskets, ProductId productId, int toRemove) {
        var basketsWithProduct = baskets.stream()
                .filter(b -> b.getReservedQuantityForProduct(productId) > 0)
                .toList();

        while (toRemove > 0 && !basketsWithProduct.isEmpty()) {
            var basket = basketsWithProduct.get((int) (Math.random() * basketsWithProduct.size()));
            int currentQuantity = basket.getReservedQuantityForProduct(productId);

            if (currentQuantity > 0) {
                int removeFromThis = Math.min(currentQuantity, toRemove);
                basket.reduceQuantity(productId, removeFromThis);
                basketRepository.save(basket);

                toRemove -= removeFromThis;
                if (basket.getReservedQuantityForProduct(productId) == 0) {
                    basketsWithProduct = basketsWithProduct.stream()
                            .filter(b -> b.getReservedQuantityForProduct(productId) > 0)
                            .toList();
                }
            }
        }
    }
}