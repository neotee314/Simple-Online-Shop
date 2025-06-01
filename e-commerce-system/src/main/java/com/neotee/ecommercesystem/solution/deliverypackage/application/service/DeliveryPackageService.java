package com.neotee.ecommercesystem.solution.deliverypackage.application.service;

import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.solution.deliverypackage.domain.DeliveryPackage;
import com.neotee.ecommercesystem.solution.deliverypackage.domain.DeliveryPackageRepository;
import com.neotee.ecommercesystem.solution.order.application.service.OrderService;
import com.neotee.ecommercesystem.solution.order.domain.Order;
import com.neotee.ecommercesystem.solution.order.domain.OrderId;
import com.neotee.ecommercesystem.solution.storageunit.application.service.InventoryFulfillmentService;
import com.neotee.ecommercesystem.solution.storageunit.domain.StorageUnit;
import com.neotee.ecommercesystem.solution.storageunit.domain.StorageUnitId;
import com.neotee.ecommercesystem.solution.thing.domain.Thing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
@RequiredArgsConstructor
public class DeliveryPackageService {

    private final DeliveryPackageRepository deliveryPackageRepository;
    private final InventoryFulfillmentService storageUnitService;
    private final OrderService orderService;


    public List<DeliveryPackage> createDeliveryPackage(OrderId orderId) {
        Map<Thing, Integer> items = orderService.getOrderLineItemsMap(orderId);
        Order order = orderService.findById(orderId);
        ZipCode clientZipCode = orderService.findClientZipCode(orderId);

        List<StorageUnitId> storageIds = storageUnitService.getContributingStorageUnit(items, clientZipCode);
        List<DeliveryPackage> deliveryPackages = new ArrayList<>();

        for (StorageUnitId storageId : storageIds) {
            StorageUnit storageUnit = storageUnitService.findById(storageId.getId());

            Map<Thing, Integer> partItems = storageUnit.getServableItems(items);


            if (partItems.isEmpty()) continue;

            DeliveryPackage deliveryPackage = new DeliveryPackage(storageUnit, order);
            deliveryPackage.createParts(partItems);

            storageUnitService.removeFromStock(partItems);

            items.keySet().removeAll(partItems.keySet());

            deliveryPackageRepository.save(deliveryPackage);
            deliveryPackages.add(deliveryPackage);

            if (items.isEmpty()) break;
        }

        return deliveryPackages;
    }

}
