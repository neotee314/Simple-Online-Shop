package com.neotee.ecommercesystem.shopsystem.storageunit.application.service;

import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.model.StorageUnit;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.service.StorageUnitFulfillmentServiceInterface;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class StorageUnitFulfillmentService implements StorageUnitFulfillmentServiceInterface {

    @Override
    public boolean canFulfill(StorageUnit storageUnit, Map<Product, Integer> items) {
        return items.entrySet()
                .stream()
                .allMatch(entry ->
                        hasSufficienQuantity(storageUnit,
                                entry.getKey(),
                                entry.getValue()
                        )
                );

    }

    public boolean hasSufficienQuantity(StorageUnit storageUnit, Product product, int requiredQuantity) {
        if (requiredQuantity < 0) {
            return false;
        }
        var stockLevel = storageUnit.findStockLevelByProduct(product);

        return stockLevel != null
                && stockLevel.getQuantityInStock() >= requiredQuantity;
    }

    @Override
    public Map<Product, Integer> getServableItems(StorageUnit storageUnit, Map<Product, Integer> requiredItems) {
        if (requiredItems == null || requiredItems.isEmpty()) {
            return new LinkedHashMap<>();
        }

        var result = new LinkedHashMap<Product, Integer>();

        for (var entry : requiredItems.entrySet()) {
            var quantity = Math.min(storageUnit.getStockOf(entry.getKey()),
                    entry.getValue());
            if (quantity > 0) {
                result.put(entry.getKey(), quantity);
            }
        }

        return result;

    }

}