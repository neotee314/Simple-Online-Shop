package com.neotee.ecommercesystem.shopsystem.deliverypackage.application.service;

import com.neotee.ecommercesystem.domainprimitives.*;
import com.neotee.ecommercesystem.exceptions.EntityNotFoundException;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.model.DeliveryPackage;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.repository.DeliveryPackageRepository;
import com.neotee.ecommercesystem.shopsystem.order.application.service.OrderApplicationService;
import com.neotee.ecommercesystem.shopsystem.storageunit.application.service.StorageUnitApplicationService;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.model.StorageUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DeliveryPackageApplicationService {

    private final DeliveryPackageRepository deliveryPackageRepository;
    private final OrderApplicationService orderApplicationService;
    private final StorageUnitApplicationService storageUnitApplicationService;
    private final DeliveryPackageAllocationService domainService;

    public void createDeliveryPackages(OrderId orderId) {
        var order = orderApplicationService.findById(orderId);
        var storageUnits = storageUnitApplicationService.findAll();
        var packages = domainService.allocateDeliveryPackageToOrder(order, storageUnits);

        deliveryPackageRepository.saveAll(packages);

    }

    public Map<ProductId, Integer> getItemsForOrderAndStorageUnit(OrderId orderId, StorageUnitId storageUnitId) {
        return deliveryPackageRepository.findByOrderId(orderId)
                .stream()
                .filter(p -> p.getStorageUnit().getId().equals(storageUnitId))
                .findFirst()
                .map(DeliveryPackage::getItems)
                .orElseThrow(() -> new EntityNotFoundException("DeliveryPackageApplicationService", "Lieferpaket nicht gefunden."));
    }


    public List<StorageUnit> getContributingStorageUnitsForOrder(OrderId orderId) {
        var packages = deliveryPackageRepository.findByOrderId(orderId);

        if (packages.isEmpty()) {
            throw new EntityNotFoundException(
                    "DeliveryPackageApplicationService",
                    "Keine Lieferpakete für die Bestellung gefunden."
            );
        }

        return packages.stream()
                .map(DeliveryPackage::getStorageUnit)
                .toList();
    }

    public List<DeliveryPackage> findByOrderId(OrderId orderId) {
        return deliveryPackageRepository.findByOrderId(orderId);
    }

    public DeliveryPackage findByOrderIdAndStorageUnitId(OrderId orderId, StorageUnitId storageUnitId) {
        return deliveryPackageRepository
                .findByOrderIdAndStorageUnitId(orderId, storageUnitId)
                .orElseThrow(() -> new EntityNotFoundException("DeliveryPackageApplicationService", "Lieferpaket nicht gefunden."));
    }

    public List<DeliveryPackageId> getDeliveryPackagesForOrder(OrderId orderId) {
        return deliveryPackageRepository.findByOrderId(orderId).stream()
                .map(DeliveryPackage::getId)
                .toList();
    }

    public DeliveryPackage findById(DeliveryPackageId id) {
        return deliveryPackageRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("DeliveryPackageApplicationService", "Lieferpaket nicht gefunden."));
    }

    public DeliveryPackageStatus getStatus(DeliveryPackageId id) {
        return findById(id).getStatus();
    }

    public void updateStatus(DeliveryPackageId id, DeliveryPackageStatus status) {
        var deliveryPackage = findById(id);
        deliveryPackage.updateStatus(status);
        deliveryPackageRepository.save(deliveryPackage);
    }

    public void deleteAllDeliveryPackages() {
        deliveryPackageRepository.deleteAll();
    }
}