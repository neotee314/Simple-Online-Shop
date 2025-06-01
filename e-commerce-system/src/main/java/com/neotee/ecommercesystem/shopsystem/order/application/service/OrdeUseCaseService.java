package com.neotee.ecommercesystem.shopsystem.order.application.service;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.shopsystem.order.domain.Order;
import com.neotee.ecommercesystem.shopsystem.order.domain.OrderId;
import com.neotee.ecommercesystem.shopsystem.order.domain.OrderPart;
import com.neotee.ecommercesystem.shopsystem.order.domain.OrderRepository;
import com.neotee.ecommercesystem.usecases.OrderUseCases;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OrdeUseCaseService implements OrderUseCases {

    private final OrderRepository orderRepository;
    private final ClientOrderServiceInterface clientOrderServiceInterface;

    @Override
    @Transactional
    public Map<UUID, Integer> getOrderHistory(EmailType clientEmail) {
        List<UUID> orderHistory = clientOrderServiceInterface.getOrderHistory((Email) clientEmail);
        if (orderHistory.isEmpty()) return new HashMap<>();

        Map<UUID, Integer> orderHistoryMap = new HashMap<>();
        List<Order> orders = mapToOrder(orderHistory);

        for (Order order : orders) {
            List<OrderPart> orderParts = order.getOrderParts();

            for (OrderPart orderPart : orderParts) {
                UUID thingId = orderPart.getThingId().getId();
                int quantity = orderPart.getOrderQuantity();
                orderHistoryMap.merge(thingId, quantity, Integer::sum);
            }
        }

        return orderHistoryMap;
    }

    private List<Order> mapToOrder(List<UUID> orderListIds) {
        List<Order> orders = new ArrayList<>();
        for (UUID orderId : orderListIds) {
            Order order = orderRepository.findById(new OrderId(orderId)).orElse(null);
            if (order == null) continue;
            orders.add(order);
        }
        return orders;
    }

    @Override
    public void deleteAllOrders() {
        orderRepository.deleteAll();
    }
}
