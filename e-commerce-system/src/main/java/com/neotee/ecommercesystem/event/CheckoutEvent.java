package com.neotee.ecommercesystem.event;

import com.neotee.ecommercesystem.shopsystem.client.domain.Client;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;

import java.util.Map;

public record CheckoutEvent(Client client, Map<Product, Integer> items) {
}