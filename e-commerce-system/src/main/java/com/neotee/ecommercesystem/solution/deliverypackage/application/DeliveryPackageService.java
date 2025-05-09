package com.neotee.ecommercesystem.solution.deliverypackage.application;

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
    private final ThingService thingService;

    private final OrderService orderService;

    @Transactional
    public List<DeliveryPackage> createDeliveryPackage(UUID orderId) {

        List<UUID> storageIds = storageUnitService.getContributingStorageUnit(orderId);
        List<DeliveryPackage> deliveryPackages = new ArrayList<>();
        Map<UUID, Integer> remainingItems = orderService.getOrderPartWithQuantity(orderId);


        for (UUID storageId : storageIds) {
            StorageUnit storageUnit = storageUnitService.findById(storageId);
            Map<UUID, Integer> contributingPart = storageUnit.getServableItems(remainingItems);

            if (contributingPart.isEmpty()) continue;

            // Create delivery package
            DeliveryPackage deliveryPackage = new DeliveryPackage(storageId, orderId);
            deliveryPackage.createParts(contributingPart);


            // Remove served items from remaining
            remainingItems.keySet().removeAll(contributingPart.keySet());

            // Save the delivery package
            deliveryPackages.add(deliveryPackage);
            deliveryPackageRepository.save(deliveryPackage);

            thingService.removeAllFromStock(contributingPart);
            storageUnitService.removeFromStock(contributingPart);
            // Stop if all items are served
            if (remainingItems.isEmpty()) break;
        }

        return deliveryPackages;

    }






}
