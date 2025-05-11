package com.neotee.ecommercesystem.solution.deliverypackage.application.service;

import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.solution.deliverypackage.domain.DeliveryPackage;
import com.neotee.ecommercesystem.solution.deliverypackage.domain.DeliveryPackageRepository;
import com.neotee.ecommercesystem.solution.order.application.service.OrderService;
import com.neotee.ecommercesystem.solution.storageunit.application.service.InventoryFulfillmentService;
import com.neotee.ecommercesystem.solution.storageunit.domain.StorageUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

@Service
@RequiredArgsConstructor
public class DeliveryPackageService {

    private final DeliveryPackageRepository deliveryPackageRepository;
    private final InventoryFulfillmentService storageUnitService;
    private final OrderService orderService;

    @Transactional
    public List<DeliveryPackage> createDeliveryPackage(UUID orderId) {
        Map<UUID, Integer> items = orderService.getOrderLineItemsMap(orderId);
        ZipCode clientZipCode = orderService.findClientZipCode(orderId);

        List<UUID> storageIds = storageUnitService.getContributingStorageUnit(items, clientZipCode);
        List<DeliveryPackage> deliveryPackages = new ArrayList<>();

        for (UUID storageId : storageIds) {
            StorageUnit storageUnit = storageUnitService.findById(storageId);
            Map<UUID, Integer> partItems = storageUnit.getServableItems(items);

            if (partItems.isEmpty()) continue;

            DeliveryPackage deliveryPackage = new DeliveryPackage(storageId, orderId);
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
