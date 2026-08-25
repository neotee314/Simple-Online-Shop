package com.neotee.ecommercesystem.shopsystem.order.application.service;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.OrderId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.order.domain.Order;
import com.neotee.ecommercesystem.shopsystem.order.domain.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderApplicationService {

    private final OrderRepository orderRepository;



    public Order findById(OrderId orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainValidationException("OrderApplicationService", "Bestellung nicht gefunden."));
    }

    public List<Order> findByClientEmail(Email clientEmail) {
        return orderRepository.findByClientEmail(clientEmail);
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