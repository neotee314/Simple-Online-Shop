package com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.service;

import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.model.DeliveryPackage;
import com.neotee.ecommercesystem.shopsystem.order.domain.Order;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.model.StorageUnit;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.service.StorageUnitComparator;

import java.util.*;

public class DeliveryPackageDomainService {

    public List<DeliveryPackage> allocate(Order order, List<StorageUnit> storageUnits) {
        var remainingItems = new HashMap<>(order.getOrderLineItemsMap());
        var prioritizedStorageUnits = prioritizeStorageUnits(storageUnits, remainingItems, order.getClientZipCode());

        var completeStorageUnit = findCompleteStorageUnit(prioritizedStorageUnits, remainingItems);

        if (completeStorageUnit.isPresent()) {
            return List.of(createPackage(completeStorageUnit.get(), order, remainingItems));
        }

        return allocateAcrossStorageUnits(order, prioritizedStorageUnits, remainingItems);
    }

    private List<StorageUnit> prioritizeStorageUnits(List<StorageUnit> storageUnits, Map<Product, Integer> items, ZipCode clientZipCode) {
        return storageUnits.stream()
                .sorted(StorageUnitComparator.forOrder(items, clientZipCode))
                .toList();
    }

    private Optional<StorageUnit> findCompleteStorageUnit(List<StorageUnit> storageUnits, Map<Product, Integer> items) {
        return storageUnits.stream()
                .filter(storageUnit -> storageUnit.canFulfill(items))
                .findFirst();
    }

    private DeliveryPackage createPackage(StorageUnit storageUnit, Order order, Map<Product, Integer> items) {
        var deliveryPackage = DeliveryPackage.create(storageUnit, order);
        addItemsToPackage(deliveryPackage, items);
        storageUnit.removeFromStock(items);
        return deliveryPackage;
    }

    private List<DeliveryPackage> allocateAcrossStorageUnits(Order order, List<StorageUnit> storageUnits, Map<Product, Integer> remainingItems) {
        var packages = new ArrayList<DeliveryPackage>();
        var availableStorageUnits = new ArrayList<>(storageUnits);

        while (!remainingItems.isEmpty() && !availableStorageUnits.isEmpty()) {
            availableStorageUnits.sort(StorageUnitComparator.forOrder(remainingItems, order.getClientZipCode()));

            var storageUnit = availableStorageUnits.remove(0);
            var servableItems = storageUnit.getServableItems(remainingItems);

            if (servableItems.isEmpty()) {
                continue;
            }

            var deliveryPackage = DeliveryPackage.create(storageUnit, order);
            addItemsToPackage(deliveryPackage, servableItems);
            storageUnit.removeFromStock(servableItems);
            removeAllocatedItems(remainingItems, servableItems);

            packages.add(deliveryPackage);
        }

        validateAllocation(remainingItems);

        return packages;
    }

    private void addItemsToPackage(DeliveryPackage deliveryPackage, Map<Product, Integer> items) {
        items.forEach(deliveryPackage::addPart);
    }

    private void removeAllocatedItems(Map<Product, Integer> remainingItems, Map<Product, Integer> allocatedItems) {
        allocatedItems.forEach((product, quantity) -> {
            var remainingQuantity = remainingItems.get(product) - quantity;

            if (remainingQuantity <= 0) {
                remainingItems.remove(product);
            } else {
                remainingItems.put(product, remainingQuantity);
            }
        });
    }

    private void validateAllocation(Map<Product, Integer> remainingItems) {
        if (!remainingItems.isEmpty()) {
            throw new DomainValidationException(
                    "Delivery",
                    "Die Bestellung kann nicht vollständig geliefert werden."
            );
        }
    }
}