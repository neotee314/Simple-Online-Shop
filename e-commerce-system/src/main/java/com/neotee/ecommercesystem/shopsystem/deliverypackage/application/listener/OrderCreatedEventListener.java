package com.neotee.ecommercesystem.shopsystem.deliverypackage.application.listener;

import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.service.DeliveryPackageApplicationService;
import com.neotee.ecommercesystem.shopsystem.order.domain.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderCreatedEventListener {

    private final DeliveryPackageApplicationService deliveryPackageService;

    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {

        deliveryPackageService.createDeliveryPackages(event.orderId());

    }
}