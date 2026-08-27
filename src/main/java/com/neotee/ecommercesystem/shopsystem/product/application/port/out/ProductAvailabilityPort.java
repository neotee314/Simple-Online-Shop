package com.neotee.ecommercesystem.shopsystem.product.application.port.out;

import com.neotee.ecommercesystem.shopsystem.product.domain.Product;


public interface ProductAvailabilityPort {
    Boolean isInStock(Product product);
    void deleteAllStockLevel();
}
