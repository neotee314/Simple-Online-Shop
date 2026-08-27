package com.neotee.ecommercesystem.shopsystem.order.application.api;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.order.application.service.OrderApplicationService;
import com.neotee.ecommercesystem.usecases.OrderUseCases;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MyFantasticOrderUseCaseService implements OrderUseCases {

    private final OrderApplicationService orderApplicationService;

    @Override
    @Transactional
    public Map<UUID, Integer> getOrderHistory(EmailType clientEmail) {
        if (clientEmail == null)
            throw new DomainValidationException("MyFantasticOrderUseCaseService", "Client Email darf nicht null sein.");

        var clientOrders = orderApplicationService.findByClientEmail((Email) clientEmail);
        if (clientOrders.isEmpty())
            return new HashMap<>();

        var orderHistoryMap = new HashMap<UUID, Integer>();

        for (var order : clientOrders) {
            if (order == null) continue;

            var orderParts = order.getOrderParts();
            for (var orderPart : orderParts) {
                var productId = orderPart.getProduct().getId().getId();
                var quantity = orderPart.getOrderQuantity();
                orderHistoryMap.merge(productId, quantity, Integer::sum);
            }
        }

        return orderHistoryMap;
    }

    @Override
    public void deleteAllOrders() {
        orderApplicationService.deleteAllOrders();
    }
}