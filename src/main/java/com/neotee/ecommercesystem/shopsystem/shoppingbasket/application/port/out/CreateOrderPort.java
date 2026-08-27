package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.port.out;

import com.neotee.ecommercesystem.domainprimitives.OrderId;
import com.neotee.ecommercesystem.shopsystem.client.domain.Client;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;

import java.util.Map;

public interface CreateOrderPort {
    OrderId createOrder(Client client, Map<Product, Integer> items);
}