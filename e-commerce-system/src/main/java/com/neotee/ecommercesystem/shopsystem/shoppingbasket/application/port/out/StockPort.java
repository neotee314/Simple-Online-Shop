package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.port.out;

import com.neotee.ecommercesystem.shopsystem.product.domain.Product;

public interface StockPort {
    int getAvailableStock(Product productId);
}