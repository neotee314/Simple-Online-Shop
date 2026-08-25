package com.neotee.ecommercesystem.shopsystem.product.application.port.out;

import com.neotee.ecommercesystem.shopsystem.product.domain.Product;


public interface ProductOrderHistoryPort {

    boolean isPartOfCompletedOrder(Product product);
}
