package com.neotee.ecommercesystem.shopsystem.order.application.service;

import com.neotee.ecommercesystem.domainprimitives.OrderStatus;
import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.shopsystem.order.domain.OrderRepository;
import com.neotee.ecommercesystem.shopsystem.product.application.port.out.ProductOrderHistoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductOrderHistoryPortAdapter implements ProductOrderHistoryPort {
    private final OrderRepository orderRepository;

    @Override
    public boolean isPartOfCompletedOrder(ProductId productId) {
        return orderRepository.findAll().stream()
                .anyMatch(order -> order.containsProduct(productId.getId()) &&
                        order.getStatus() == OrderStatus.DELIVERED);
    }
}
