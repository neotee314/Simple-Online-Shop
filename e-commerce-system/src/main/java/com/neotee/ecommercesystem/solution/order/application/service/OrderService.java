package com.neotee.ecommercesystem.solution.order.application.service;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.solution.client.application.service.ClientService;
import com.neotee.ecommercesystem.solution.order.domain.Order;
import com.neotee.ecommercesystem.solution.order.domain.OrderRepository;
import com.neotee.ecommercesystem.solution.thing.application.service.OrderedItemsServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService implements OrderedItemsServiceInterface {

    private final OrderRepository orderRepository;
    private final ClientService clientService;

    // 1. Create a new order
    @Transactional
    public UUID createOrder(Map<UUID, Integer> partsWithQuantity, Email clientEmail) {
        // Create a new order
        Order order = new Order(clientEmail);

        // Add order parts to the order
        order.addOrderParts(partsWithQuantity);

        //Add Order to OrderHistory of Client
        clientService.addToOrderHistory(clientEmail,order.getOrderId());

        // Save the order
        orderRepository.save(order);
        return order.getOrderId();
    }



    // 4. Check if an item exists in the order
    public boolean contains(UUID orderId, UUID thingId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ShopException("Order not found"));

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



    public ZipCode findClientZipCode(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ShopException("Order not found"));
        Email clientEmail = order.getClientEmail();
        ZipCode zipCode = clientService.findClientZipCode(clientEmail);
        return zipCode;

    }


    @Transactional
    public Map<UUID, Integer> getOrderLineItemsMap(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ShopException("Order not found"));
        return order.getOrderLineItemsMap();
    }

    public Order findById(UUID orderId) {
        if (orderId == null) throw new ShopException("orderId is null");
        return orderRepository.findById(orderId).orElse(null);
    }
}
