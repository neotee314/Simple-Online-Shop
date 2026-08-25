package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.port.out;

import com.neotee.ecommercesystem.domainprimitives.OrderId;
import com.neotee.ecommercesystem.shopsystem.client.domain.Client;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;

public interface CreateOrderPort {
    OrderId createOrder(Client client, Product product, Integer quantity);

    void addOrderPart(OrderId orderId, Client client, Product product, Integer quantity);

    void submitOrder(OrderId orderId);
}