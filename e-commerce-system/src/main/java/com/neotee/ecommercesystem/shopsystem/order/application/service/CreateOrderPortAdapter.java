package com.neotee.ecommercesystem.shopsystem.order.application.service;

import com.neotee.ecommercesystem.domainprimitives.OrderId;
import com.neotee.ecommercesystem.shopsystem.client.domain.Client;
import com.neotee.ecommercesystem.shopsystem.order.domain.Order;
import com.neotee.ecommercesystem.shopsystem.order.domain.OrderPart;
import com.neotee.ecommercesystem.shopsystem.order.domain.OrderRepository;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.port.out.CreateOrderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateOrderPortAdapter implements CreateOrderPort {
    private final OrderRepository orderRepository;

    @Override
    public OrderId createOrder(Client client, Product product, Integer quantity) {
        var order = Order.create(client);
        var orderPart = OrderPart.create(product, quantity);
        order.addOrderPart(orderPart);
        return orderRepository.save(order).getId();
    }

}
