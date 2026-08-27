package com.neotee.ecommercesystem.shopsystem.storageunit.domain.service;

import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.model.StorageUnit;

import java.util.Comparator;
import java.util.Map;

public final class StorageUnitComparator {

    private StorageUnitComparator() {
    }

    public static Comparator<StorageUnit> forOrder(
            Map<Product, Integer> items,
            ZipCode clientZipCode
    ) {
        return Comparator
                .comparingDouble(
                        (StorageUnit unit) ->
                                -unit.getTotalWeightOfServableItems(items)
                )
                .thenComparingInt(
                        unit ->
                                unit.getDistanceToClient(clientZipCode)
                );
    }
}