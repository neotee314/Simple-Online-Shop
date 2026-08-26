package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.port.out;

import com.neotee.ecommercesystem.domainprimitives.ProductId;

public interface StockPort {
    void decreaseStock(ProductId productId, int quantity);
    boolean hasEnoughStock(ProductId productId, int quantity);
    int getAvailableStock(ProductId productId);
}