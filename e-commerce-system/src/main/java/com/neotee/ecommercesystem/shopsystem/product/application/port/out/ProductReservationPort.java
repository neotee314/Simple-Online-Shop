package com.neotee.ecommercesystem.shopsystem.product.application.port.out;


import com.neotee.ecommercesystem.domainprimitives.ProductId;

public interface ProductReservationPort {

    boolean isReservedInAnyBasket(ProductId productId);

    void deleteShoppingBasketParts();
}
