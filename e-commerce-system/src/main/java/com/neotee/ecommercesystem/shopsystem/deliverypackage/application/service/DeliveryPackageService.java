package com.neotee.ecommercesystem.shopsystem.deliverypackage.application.service;

import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.DeliveryPackage;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.DeliveryPackageRepository;
import com.neotee.ecommercesystem.shopsystem.order.application.service.OrderService;
import com.neotee.ecommercesystem.shopsystem.order.domain.Order;
import com.neotee.ecommercesystem.shopsystem.order.domain.OrderId;
import com.neotee.ecommercesystem.shopsystem.storageunit.application.service.InventoryFulfillmentService;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnit;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnitId;
import com.neotee.ecommercesystem.shopsystem.thing.domain.Thing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
@RequiredArgsConstructor
public class DeliveryPackageService {

    private final DeliveryPackageRepository deliveryPackageRepository;
    private final InventoryFulfillmentService inventoryFulfillmentService;
    private final OrderService orderService;


    public List<DeliveryPackage> createDeliveryPackage(OrderId orderId) {
        Map<Thing, Integer> items = orderService.getOrderLineItemsMap(orderId);
        Order order = orderService.findById(orderId);
        ZipCode clientZipCode = orderService.findClientZipCode(orderId);


        List<StorageUnitId> storageIds = inventoryFulfillmentService.getContributingStorageUnit(items, clientZipCode);
        List<DeliveryPackage> deliveryPackages = new ArrayList<>();

        for (StorageUnitId storageId : storageIds) {
            StorageUnit storageUnit = inventoryFulfillmentService.findById(storageId.getId());

            Map<Thing, Integer> partItems = storageUnit.getServableItems(items);

            if (partItems.isEmpty()) break;

            DeliveryPackage deliveryPackage = new DeliveryPackage(storageUnit, order);
            deliveryPackage.createParts(partItems);

            inventoryFulfillmentService.removeFromStock(storageUnit, partItems);

            items.keySet().removeAll(partItems.keySet());

            deliveryPackageRepository.save(deliveryPackage);
            deliveryPackages.add(deliveryPackage);

            if (items.isEmpty()) break;
        }

        return deliveryPackages;
    }

}
