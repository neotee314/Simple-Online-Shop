package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.port.out;

import com.neotee.ecommercesystem.domainprimitives.ProductId;

public interface DecreaseStockPort {
    void decreaseStock(ProductId productId, int quantity);
}