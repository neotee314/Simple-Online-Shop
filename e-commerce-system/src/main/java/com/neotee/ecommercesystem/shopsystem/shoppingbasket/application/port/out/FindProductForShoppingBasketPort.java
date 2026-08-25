package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.port.out;

import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;

public interface FindProductForShoppingBasketPort {
    Product findById(ProductId productId);
}