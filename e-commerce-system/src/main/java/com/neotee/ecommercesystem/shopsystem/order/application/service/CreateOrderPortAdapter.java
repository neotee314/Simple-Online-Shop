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

@Component
@RequiredArgsConstructor
public class CreateOrderPortAdapter implements CreateOrderPort {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public OrderId createOrder(Client client, Product product, Integer quantity) {
        var order = Order.create(client);
        var orderPart = OrderPart.create(product, quantity);
        order.addOrderPart(orderPart);
        var savedOrder = orderRepository.save(order);
        publishEvents(savedOrder);
        return savedOrder.getId();
    }

    @Override
    public void addOrderPart(OrderId orderId, Client client, Product product, Integer quantity) {
        var order = orderRepository.findById(orderId)
                .orElseGet(() -> {
                    var newOrder = Order.create(client);
                    return orderRepository.save(newOrder);
                });

        var orderPart = OrderPart.create(product, quantity);
        order.addOrderPart(orderPart);
        orderRepository.save(order);
    }

    @Override
    public void submitOrder(OrderId orderId) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("CreateOrderPortAdapter", "Order nicht gefunden."));
        order.submit();
        orderRepository.save(order);
    }

    private void publishEvents(Order order) {
        for (Object event : order.getDomainEvents()) {
            eventPublisher.publishEvent(event);
        }
        order.clearEvents();
    }
}