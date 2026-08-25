package com.neotee.ecommercesystem.shopsystem.storageunit.application.port.out;

import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;

public interface FindProductForStorageUnitPort {
    Product findById(ProductId productId);
}