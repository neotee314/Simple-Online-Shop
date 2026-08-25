package com.neotee.ecommercesystem.shopsystem.product.application.port.out;

import com.neotee.ecommercesystem.domainprimitives.ProductId;



public interface ProductAvailabilityPort {
    Boolean isInStock(ProductId productId);
    void deleteAllStockLevel();
}
