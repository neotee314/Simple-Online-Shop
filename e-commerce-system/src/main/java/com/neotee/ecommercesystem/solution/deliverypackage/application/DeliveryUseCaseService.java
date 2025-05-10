package com.neotee.ecommercesystem.solution.deliverypackage.application;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.solution.deliverypackage.domain.DeliveryPackage;
import com.neotee.ecommercesystem.solution.deliverypackage.domain.DeliveryPackagePart;
import com.neotee.ecommercesystem.solution.deliverypackage.domain.DeliveryPackageRepository;
import com.neotee.ecommercesystem.usecases.DeliveryPackageUseCases;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DeliveryUseCaseService implements DeliveryPackageUseCases {

    private final DeliveryPackageRepository deliveryPackageRepository;

    @Override
    @Transactional
    public List<UUID> getContributingStorageUnitsForOrder(UUID orderId) {
        // Retrieve all DeliveryPackages related to the given orderId
        List<DeliveryPackage> deliveryPackages = deliveryPackageRepository.findByOrderId(orderId);
        if (deliveryPackages.isEmpty()) throw new ShopException("Delivery package does not exist");

        // List to store IDs of storage units contributing to this order
        List<UUID> contributingStorageUnits = new ArrayList<>();

        // Add the storage unit ID of each DeliveryPackage to the list
        for (DeliveryPackage deliveryPackage : deliveryPackages) {
            UUID storageUnitId = deliveryPackage.getStorageUnitId();

            // Add only if not already in the list
            if (!contributingStorageUnits.contains(storageUnitId)) {
                contributingStorageUnits.add(storageUnitId);
            }
        }

        return contributingStorageUnits;
    }

    @Override
    @Transactional
    public Map<UUID, Integer> getDeliveryPackageForOrderAndStorageUnit(UUID orderId, UUID storageUnitId) {
        // Retrieve DeliveryPackages for the given orderId
        List<DeliveryPackage> deliveryPackages = deliveryPackageRepository.findByOrderId(orderId);
        if (deliveryPackages.isEmpty()) throw new ShopException("Delivery package does not exist");

        Map<UUID, Integer> deliveryPackageMap = new HashMap<>();

        // Find the DeliveryPackages related to the given storage unit and order
        for (DeliveryPackage deliveryPackage : deliveryPackages) {
            if (deliveryPackage.hasStorage(storageUnitId)) {
                // Add each part of the delivery package to the map
                for (DeliveryPackagePart part : deliveryPackage.getParts()) {
                    deliveryPackageMap.put(part.getThingId(), part.getQuantity());
                }
            }
        }

        return deliveryPackageMap;
    }

    @Override
    public void deleteAllDeliveryPackages() {
        deliveryPackageRepository.deleteAll();
    }
}