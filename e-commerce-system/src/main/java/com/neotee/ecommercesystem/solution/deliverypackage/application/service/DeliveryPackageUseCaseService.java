package com.neotee.ecommercesystem.solution.deliverypackage.application.service;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.solution.deliverypackage.domain.DeliveryPackage;
import com.neotee.ecommercesystem.solution.deliverypackage.domain.DeliveryPackagePart;
import com.neotee.ecommercesystem.solution.deliverypackage.domain.DeliveryPackageRepository;
import com.neotee.ecommercesystem.solution.order.domain.OrderId;
import com.neotee.ecommercesystem.usecases.DeliveryPackageUseCases;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DeliveryPackageUseCaseService implements DeliveryPackageUseCases {

    private final DeliveryPackageRepository deliveryPackageRepository;

    @Override
    @Transactional
    public List<UUID> getContributingStorageUnitsForOrder(UUID orderId) {
        // Retrieve all DeliveryPackages related to the given orderId
        OrderId id = new OrderId(orderId);
        List<DeliveryPackage> deliveryPackages = findByOrderId(id);
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
        OrderId id = new OrderId(orderId);
        List<DeliveryPackage> deliveryPackages = findByOrderId(id);
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

    private List<DeliveryPackage> findByOrderId(OrderId orderId) {
        List<DeliveryPackage> deliveryPackages = deliveryPackageRepository.findAll();
        List<DeliveryPackage> result = new ArrayList<>();
        for (DeliveryPackage deliveryPackage : deliveryPackages) {
            if (deliveryPackage.getOrder().getOrderId().equals(orderId)) {
                result.add(deliveryPackage);
            }
        }
        return result;

    }

    @Override
    public void deleteAllDeliveryPackages() {
        deliveryPackageRepository.deleteAll();
    }
}