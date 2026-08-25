package com.neotee.ecommercesystem.shopsystem.product.application.port.out;


import com.neotee.ecommercesystem.domainprimitives.ProductId;


public interface ProductOrderHistoryPort {

    boolean isPartOfCompletedOrder(ProductId productId);
}
