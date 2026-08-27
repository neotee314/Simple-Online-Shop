package com.neotee.ecommercesystem.shopsystem.order.application.service;

import com.neotee.ecommercesystem.domainprimitives.OrderId;
import com.neotee.ecommercesystem.exceptions.EntityNotFoundException;
import com.neotee.ecommercesystem.shopsystem.client.domain.Client;
import com.neotee.ecommercesystem.shopsystem.order.domain.Order;
import com.neotee.ecommercesystem.shopsystem.order.domain.OrderPart;
import com.neotee.ecommercesystem.shopsystem.order.domain.OrderRepository;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.port.out.CreateOrderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CreateOrderPortAdapter implements CreateOrderPort {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public OrderId createOrder(Client client, Map<Product, Integer> items) {
        var order = Order.create(client);

        for (var entry : items.entrySet()) {
            var orderPart = OrderPart.create(entry.getKey(), entry.getValue());
            order.addOrderPart(orderPart);
        }
        var events = new ArrayList<>(order.getDomainEvents());

        var savedOrder = orderRepository.save(order);
        for (Object event : events) {
            eventPublisher.publishEvent(event);
        }
        order.clearEvents();

        return savedOrder.getId();
    }
}