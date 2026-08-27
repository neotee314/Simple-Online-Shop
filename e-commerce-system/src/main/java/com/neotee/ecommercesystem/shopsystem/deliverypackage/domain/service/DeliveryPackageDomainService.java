package com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.service;

import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.model.DeliveryPackage;
import com.neotee.ecommercesystem.shopsystem.order.domain.Order;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.model.StorageUnit;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.service.StorageUnitComparator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DeliveryPackageDomainService {

    public List<DeliveryPackage> allocate(Order order, List<StorageUnit> storageUnits) {
        var remainingItems = new HashMap<>(order.getOrderLineItemsMap());
        var prioritizedStorageUnits = selectStorageUnitsByDeliveryPriority(storageUnits, remainingItems, order.getClientZipCode());

        var completeStorageUnit = findStorageUnitForCompleteFulfillment(prioritizedStorageUnits, remainingItems);

        if (completeStorageUnit.isPresent()) {
            return List.of(createPackageFrom(completeStorageUnit.get(), order, remainingItems));
        }

        return splitFulfillmentAcrossStorageUnits(order, prioritizedStorageUnits, remainingItems);
    }

    private List<StorageUnit> selectStorageUnitsByDeliveryPriority(List<StorageUnit> storageUnits, Map<Product, Integer> items, ZipCode clientZipCode) {
        return storageUnits.stream()
                .sorted(StorageUnitComparator.forOrder(items, clientZipCode))
                .toList();
    }

    private Optional<StorageUnit> findStorageUnitForCompleteFulfillment(List<StorageUnit> storageUnits, Map<Product, Integer> items) {
        return storageUnits.stream()
                .filter(storageUnit -> storageUnit.canFulfill(items))
                .findFirst();
    }

    private DeliveryPackage createPackageFrom(StorageUnit storageUnit, Order order, Map<Product, Integer> items) {
        var deliveryPackage = DeliveryPackage.create(storageUnit, order);
        addItemsToPackage(deliveryPackage, items);
        storageUnit.removeFromStock(items);
        return deliveryPackage;
    }

    private List<DeliveryPackage> splitFulfillmentAcrossStorageUnits(Order order, List<StorageUnit> storageUnits, Map<Product, Integer> remainingItems) {
        var packages = new ArrayList<DeliveryPackage>();
        var availableStorageUnits = new ArrayList<>(storageUnits);

        while (!remainingItems.isEmpty() && !availableStorageUnits.isEmpty()) {
            availableStorageUnits.sort(StorageUnitComparator.forOrder(remainingItems, order.getClientZipCode()));

            var storageUnit = availableStorageUnits.removeFirst();
            var servableItems = storageUnit.getServableItems(remainingItems);

            if (servableItems.isEmpty()) {
                continue;
            }

            var deliveryPackage = DeliveryPackage.create(storageUnit, order);
            addItemsToPackage(deliveryPackage, servableItems);
            storageUnit.removeFromStock(servableItems);
            applyFulfillment(remainingItems, servableItems);

            packages.add(deliveryPackage);
        }

        ensureOrderIsFullyFulfilled(remainingItems);

        return packages;
    }

    private void addItemsToPackage(DeliveryPackage deliveryPackage, Map<Product, Integer> items) {
        items.forEach(deliveryPackage::addPart);
    }

    private void applyFulfillment(Map<Product, Integer> remainingItems, Map<Product, Integer> fulfilledItems) {
        fulfilledItems.forEach((product, quantity) -> {
            var remainingQuantity = remainingItems.get(product) - quantity;

            if (remainingQuantity <= 0) {
                remainingItems.remove(product);
            } else {
                remainingItems.put(product, remainingQuantity);
            }
        });
    }

    private void ensureOrderIsFullyFulfilled(Map<Product, Integer> remainingItems) {
        if (!remainingItems.isEmpty()) {
            throw new DomainValidationException(
                    "Delivery",
                    "The order cannot be fully fulfilled."
            );
        }
    }
}