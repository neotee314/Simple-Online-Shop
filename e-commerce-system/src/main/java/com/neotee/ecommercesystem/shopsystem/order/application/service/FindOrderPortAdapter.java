package com.neotee.ecommercesystem.shopsystem.order.application.service;

import com.neotee.ecommercesystem.domainprimitives.OrderId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.port.out.FindOrderPort;
import com.neotee.ecommercesystem.shopsystem.order.domain.Order;
import com.neotee.ecommercesystem.shopsystem.order.domain.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FindOrderPortAdapter implements FindOrderPort {

    private final OrderRepository orderRepository;

    @Override
    public Order findById(OrderId orderId) {
        if (orderId == null)
            throw new DomainValidationException("FindOrderPortAdapter", "Order ID darf nicht null sein.");

        return orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainValidationException("FindOrderPortAdapter", "Order nicht gefunden."));
    }
}