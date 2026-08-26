package com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.service;

import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.DeliveryPackage;
import com.neotee.ecommercesystem.shopsystem.order.domain.Order;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnit;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnitComparator;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DeliveryPackageDomainService {

    public List<DeliveryPackage> createPackages(
            Order order,
            List<StorageUnit> storageUnits
    ) {
        System.out.println("\n========== DOMAIN SERVICE DEBUG ==========");
        System.out.println("Order ID: " + order.getId());
        System.out.println("Order items:");

        order.getOrderLineItemsMap().forEach((product, quantity) -> {
            System.out.println(
                    "  Product ID=" + product.getId()
                            + " name=" + product.getName()
                            + " quantity=" + quantity
            );
        });

        System.out.println("\nStorage units BEFORE sorting:");

        for (StorageUnit unit : storageUnits) {
            System.out.println(
                    "  StorageUnit ID=" + unit.getId()
                            + " stock=" + unit.getStockLevels()
            );
        }

        var items = new HashMap<>(
                order.getOrderLineItemsMap()
        );
        for (StorageUnit unit : storageUnits) {
            System.out.println(
                    "SU=" + unit.getId()
                            + " contribution=" + unit.getContributingItemCount(items)
                            + " distance=" + unit.getDistanceToClient(order.getClientZipCode())
            );
        }
        var sorted = sortStorageUnits(
                storageUnits,
                items,
                order.getClientZipCode()
        );

        System.out.println("\nStorage units AFTER sorting:");

        for (int i = 0; i < sorted.size(); i++) {
            StorageUnit unit = sorted.get(i);

            System.out.println(
                    "  sorted[" + i + "]"
                            + " ID=" + unit.getId()
                            + " stock=" + unit.getStockLevels()
            );

            System.out.println(
                    "    canFulfill=" + unit.canFulfill(items)
            );

            System.out.println(
                    "    servableItems=" + unit.getServableItems(items)
            );
        }

        var completeUnit = findCompleteUnit(sorted, items);

        System.out.println("\nComplete unit:");
        System.out.println(
                completeUnit.map(unit -> unit.getId().toString())
                        .orElse("NONE")
        );

        if (completeUnit.isPresent()) {
            System.out.println("Using COMPLETE storage unit.");
            System.out.println("==========================================\n");

            return List.of(
                    createPackage(
                            completeUnit.get(),
                            order,
                            items
                    )
            );
        }

        System.out.println("No complete unit. Creating MULTIPLE packages.");

        var result = createMultiplePackages(
                order,
                sorted,
                items
        );

        System.out.println("==========================================\n");

        return result;
    }

    private List<StorageUnit> sortStorageUnits(
            List<StorageUnit> storageUnits,
            Map<Product, Integer> items,
            ZipCode clientZipCode
    ) {
        return storageUnits.stream()
                .sorted(
                        StorageUnitComparator.forOrder(
                                items,
                                clientZipCode
                        )
                )
                .toList();
    }

    private Optional<StorageUnit> findCompleteUnit(
            List<StorageUnit> storageUnits,
            Map<Product, Integer> items
    ) {
        return storageUnits.stream()
                .filter(unit -> unit.canFulfill(items))
                .findFirst();
    }

    private DeliveryPackage createPackage(
            StorageUnit storageUnit,
            Order order,
            Map<Product, Integer> items
    ) {
        var deliveryPackage =
                DeliveryPackage.create(
                        storageUnit,
                        order
                );

        deliveryPackage.addParts(items);
        storageUnit.removeFromStock(items);

        return deliveryPackage;
    }

    private List<DeliveryPackage> createMultiplePackages(
            Order order,
            List<StorageUnit> storageUnits,
            Map<Product, Integer> remainingItems
    ) {
        var packages = new ArrayList<DeliveryPackage>();
        var remainingStorageUnits = new ArrayList<>(storageUnits);

        System.out.println("\n----- createMultiplePackages -----");

        while (!remainingItems.isEmpty() && !remainingStorageUnits.isEmpty()) {
            // ✅ هر بار دوباره مرتب کن
            remainingStorageUnits.sort(
                    StorageUnitComparator.forOrder(
                            remainingItems,
                            order.getClientZipCode()
                    )
            );

            var storageUnit = remainingStorageUnits.remove(0);

            System.out.println("\nChecking StorageUnit: " + storageUnit.getId());
            System.out.println("Remaining items BEFORE: " + remainingItems);

            var servableItems = storageUnit.getServableItems(remainingItems);

            System.out.println("Servable items: " + servableItems);

            if (servableItems.isEmpty()) {
                System.out.println("Nothing servable -> CONTINUE");
                continue;
            }

            System.out.println("Creating package from StorageUnit: " + storageUnit.getId());

            var deliveryPackage = DeliveryPackage.create(storageUnit, order);
            deliveryPackage.addParts(servableItems);
            storageUnit.removeFromStock(servableItems);

            removeServedItems(remainingItems, servableItems);

            System.out.println("Remaining items AFTER: " + remainingItems);

            packages.add(deliveryPackage);
        }

        System.out.println("\nRemaining after ALL storage units: " + remainingItems);
        validateRemainingItems(remainingItems);

        return packages;
    }

    private void addPackageFromStorageUnit(
            List<DeliveryPackage> packages,
            StorageUnit storageUnit,
            Order order,
            Map<Product, Integer> remainingItems
    ) {
        var servableItems =
                storageUnit.getServableItems(remainingItems);

        if (servableItems.isEmpty()) {
            return;
        }

        var deliveryPackage =
                DeliveryPackage.create(
                        storageUnit,
                        order
                );

        deliveryPackage.addParts(servableItems);
        storageUnit.removeFromStock(servableItems);
        removeServedItems(remainingItems, servableItems);

        packages.add(deliveryPackage);
    }

    private void removeServedItems(
            Map<Product, Integer> remainingItems,
            Map<Product, Integer> servedItems
    ) {
        for (var entry : servedItems.entrySet()) {
            var remaining =
                    remainingItems.get(entry.getKey())
                            - entry.getValue();

            if (remaining <= 0) {
                remainingItems.remove(entry.getKey());
            } else {
                remainingItems.put(
                        entry.getKey(),
                        remaining
                );
            }
        }
    }

    private void validateRemainingItems(
            Map<Product, Integer> remainingItems
    ) {
        if (!remainingItems.isEmpty()) {
            throw new DomainValidationException(
                    "Delivery",
                    "Die Bestellung kann nicht vollständig geliefert werden."
            );
        }
    }
}