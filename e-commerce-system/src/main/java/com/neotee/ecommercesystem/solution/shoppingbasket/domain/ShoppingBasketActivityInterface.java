package com.neotee.ecommercesystem.solution.shoppingbasket.domain;


import java.util.UUID;

public interface ShoppingBasketActivityInterface {
    void processEmpty();
    void processFilled();
    void processPayment(UUID paymentId);
    void processDelivery(UUID deliveryId);
}
