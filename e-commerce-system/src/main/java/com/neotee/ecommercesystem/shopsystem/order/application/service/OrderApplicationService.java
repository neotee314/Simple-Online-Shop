package com.neotee.ecommercesystem.shopsystem.order.application.service;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.OrderId;
import com.neotee.ecommercesystem.domainprimitives.OrderStatus;
import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.client.domain.Client;
import com.neotee.ecommercesystem.shopsystem.order.domain.Order;
import com.neotee.ecommercesystem.shopsystem.order.domain.OrderPart;
import com.neotee.ecommercesystem.shopsystem.order.domain.OrderRepository;
import com.neotee.ecommercesystem.shopsystem.product.application.port.out.ProductOrderHistoryPort;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.port.out.CreateOrderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderApplicationService implements ProductOrderHistoryPort, CreateOrderPort {

    private final OrderRepository orderRepository;

    @Override
    public OrderId createOrder(Client client, Product product, Integer quantity) {
        var order = Order.create(client);
        var orderPart = OrderPart.create(product, quantity);
        order.addOrderPart(orderPart);
        return orderRepository.save(order).getId();
    }


    public Order findById(OrderId orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainValidationException("OrderApplicationService", "Bestellung nicht gefunden."));
    }

    public List<Order> findByClientEmail(Email clientEmail) {
        var orders = orderRepository.findByClientEmail(clientEmail);
        if (orders.isEmpty())
            throw new DomainValidationException("OrderApplicationService", "Keine Bestellungen für diesen Client gefunden.");
        return orders;
    }

    public boolean isPartOfCompletedOrder(ProductId productId) {
        return orderRepository.findAll().stream()
                .anyMatch(order -> order.containsProduct(productId.getId()) &&
                        order.getStatus() == OrderStatus.DELIVERED);
    }


    public void submitOrder(OrderId orderId) {
        var order = findById(orderId);
        order.submit();
        orderRepository.save(order);
    }


    public void cancelOrder(OrderId orderId) {
        var order = findById(orderId);
        order.cancel();
        orderRepository.save(order);
    }


    public void deliverOrder(OrderId orderId) {
        var order = findById(orderId);
        order.deliver();
        orderRepository.save(order);
    }


    public void deleteAllOrders() {
        orderRepository.deleteAll();
    }


}