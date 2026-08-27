package com.neotee.ecommercesystem.shopsystem.deliverypackage.application.listener;

import com.neotee.ecommercesystem.event.OrderCreatedEvent;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.service.DeliveryPackageApplicationService;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCreatedEventListener {

    private final DeliveryPackageApplicationService deliveryPackageService;

    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {

        deliveryPackageService.createDeliveryPackages(event.orderId());

    }
}