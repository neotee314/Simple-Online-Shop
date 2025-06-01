package com.neotee.ecommercesystem.shopsystem.order.application.service;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.exception.EntityNotFoundException;
import com.neotee.ecommercesystem.shopsystem.order.domain.Order;
import com.neotee.ecommercesystem.shopsystem.order.domain.OrderId;
import com.neotee.ecommercesystem.shopsystem.order.domain.OrderRepository;
import com.neotee.ecommercesystem.shopsystem.thing.application.service.OrderedItemsServiceInterface;
import com.neotee.ecommercesystem.shopsystem.thing.domain.Thing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService implements OrderedItemsServiceInterface {

    private final OrderRepository orderRepository;
    private final ClientOrderServiceInterface clientOrderServiceInterface;

    // 1. Create a new order
    @Transactional
    public OrderId createOrder(Map<Thing, Integer> thingQuantityMap, Email clientEmail) {
        // Create a new order
        Order order = new Order(clientEmail);

        // Add order parts to the order
        order.addOrderParts(thingQuantityMap);

        //Add Order to OrderHistory of Client
        clientOrderServiceInterface.addToOrderHistory(clientEmail,order.getOrderId().getId());

        // Save the order
        orderRepository.save(order);
        return order.getOrderId();
    }



    // 4. Check if an item exists in the order
    public boolean contains(OrderId orderId, UUID thingId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);

        return order.contains(thingId);
    }


    @Override
    public boolean isPartOfCompletedOrder(UUID thingId) {
        return orderRepository.findAll().stream()
                .anyMatch(order -> order.contains(thingId));
    }

    @Override
    public void deleteOrderParts() {
        orderRepository.deleteAll();
    }



    public ZipCode findClientZipCode(OrderId orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        Email clientEmail = order.getClientEmail();
        ZipCode zipCode = clientOrderServiceInterface.findClientZipCode(clientEmail);
        return zipCode;

    }


    @Transactional
    public Map<Thing, Integer> getOrderLineItemsMap(OrderId orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        return order.getOrderLineItemsMap();
    }

    public Order findById(OrderId orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
    }
}
