package com.neotee.ecommercesystem.shopsystem.order.application.service;

import com.neotee.ecommercesystem.shopsystem.order.domain.OrderRepository;
import com.neotee.ecommercesystem.shopsystem.product.application.port.out.ProductOrderHistoryPort;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductOrderHistoryPortAdapter implements ProductOrderHistoryPort {
    private final OrderRepository orderRepository;

    @Override
    public boolean isPartOfCompletedOrder(Product product) {
        return orderRepository.findAll().stream()
                .anyMatch(order -> order.containsProduct(product));
    }
}
