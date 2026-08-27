package com.neotee.ecommercesystem.shopsystem.storageunit.domain.service;

import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.model.StorageUnit;

import java.util.Map;

public interface StorageUnitFulfillmentServiceInterface {

    boolean canFulfill(StorageUnit storageUnit, Map<Product, Integer> items);

    Map<Product, Integer> getServableItems(StorageUnit storageUnit, Map<Product, Integer> items);
}
