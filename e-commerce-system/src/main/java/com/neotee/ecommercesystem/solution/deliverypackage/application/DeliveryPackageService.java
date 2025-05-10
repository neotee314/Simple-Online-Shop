package com.neotee.ecommercesystem.solution.deliverypackage.application;

import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.solution.deliverypackage.domain.DeliveryPackage;
import com.neotee.ecommercesystem.solution.deliverypackage.domain.DeliveryPackageRepository;
import com.neotee.ecommercesystem.solution.order.application.OrderService;
import com.neotee.ecommercesystem.solution.storageunit.application.StorageUnitService;
import com.neotee.ecommercesystem.solution.storageunit.domain.StorageUnit;
import com.neotee.ecommercesystem.solution.thing.application.ThingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

@Service
@RequiredArgsConstructor
public class DeliveryPackageService {

    private final DeliveryPackageRepository deliveryPackageRepository;
    private final StorageUnitService storageUnitService;
    private final OrderService orderService;

    @Transactional
    public List<DeliveryPackage> createDeliveryPackage(UUID orderId) {
        Map<UUID, Integer> remainingItems = orderService.getOrderLineItemsMap(orderId);
        ZipCode clientZipCode = orderService.findClientZipCode(orderId);

        List<UUID> storageIds = storageUnitService.getContributingStorageUnit(remainingItems, clientZipCode);
        List<DeliveryPackage> deliveryPackages = new ArrayList<>();

        for (UUID storageId : storageIds) {
            StorageUnit storageUnit = storageUnitService.findById(storageId);
            Map<UUID, Integer> partItems = storageUnit.getServableItems(remainingItems);

            if (partItems.isEmpty()) continue;

            DeliveryPackage deliveryPackage = new DeliveryPackage(storageId, orderId);
            deliveryPackage.createParts(partItems);

            storageUnitService.removeFromStock(partItems);


            remainingItems.keySet().removeAll(partItems.keySet());

            deliveryPackageRepository.save(deliveryPackage);
            deliveryPackages.add(deliveryPackage);

            if (remainingItems.isEmpty()) break;
        }

        return deliveryPackages;
    }

}
