package com.neotee.ecommercesystem.solution.order.application;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.solution.client.application.ClientService;
import com.neotee.ecommercesystem.solution.order.domain.Order;
import com.neotee.ecommercesystem.solution.order.domain.OrderPart;
import com.neotee.ecommercesystem.solution.order.domain.OrderRepository;
import com.neotee.ecommercesystem.solution.thing.application.OrderedItemsServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

        // Add order parts to the order using the method in the Order class
        order.addOrderParts(partsWithQuantity);

        //Add Order to OrderHistory of Client
        clientService.addToOrderHistory(clientEmail,order.getOrderId());

        // Save the order to the repository
        orderRepository.save(order);
        return order.getOrderId();
    }

    // 2. Add an item to the order
    public void addItem(UUID orderId, UUID thingId, int quantity) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ShopException("Order not found"));

        OrderPart part = new OrderPart(thingId, quantity);
        order.addOrderPart(part);

        orderRepository.save(order);
    }

    // 3. Remove or decrease the quantity of an item
    public void removeItem(UUID orderId, UUID thingId, int quantity) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ShopException("Order not found"));

        OrderPart part = new OrderPart(thingId, quantity);
        order.removeOrderPart(part);

        orderRepository.save(order);
    }

    // 4. Check if an item exists in the order
    public boolean contains(UUID orderId, UUID thingId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ShopException("Order not found"));

        return order.contains(thingId);
    }

    // 5. Get a specific OrderPart by thingId
    public UUID getOrderPartContainingThing(UUID orderId, UUID thingId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ShopException("Order not found"));
        OrderPart orderPart = order.getOrderParts().stream()
                .filter(p -> p.contains(thingId))
                .findFirst()
                .orElse(null);
        if (orderPart == null) throw new ShopException("OrderPart not found");
        return orderPart.getId();
    }

    // 7. Check if an item is part of a completed order
    @Override
    public boolean isPartOfCompletedOrder(UUID thingId) {
        return orderRepository.findAll().stream()
                .anyMatch(order -> order.contains(thingId));
    }

    // 8. Delete all order parts (for management purposes)
    @Override
    public void deleteOrderParts() {
        orderRepository.deleteAll();
    }


    public List<UUID> getUnfulfilledItems(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ShopException("Order not found"));
        return order.getUnfulfilledItems();
    }

    public ZipCode findClientZipCode(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ShopException("Order not found"));
        Email clientEmail = order.getClientEmail();
        ZipCode zipCode = clientService.findClientZipCode(clientEmail);
        return zipCode;

    }

    public Integer getOrderQuantityOf(UUID orderId, UUID thingId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ShopException("Order not found"));
        Integer orderQuantity = order.getOrderQuantityOf(thingId);
        return orderQuantity;
    }

    @Transactional
    public Map<UUID, Integer> getOrderPartWithQuantity(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ShopException("Order not found"));
        return order.getPartsWithQuantity();
    }

    public Order findById(UUID orderId) {
        if (orderId == null) throw new ShopException("orderId is null");
        return orderRepository.findById(orderId).orElse(null);
    }
}
