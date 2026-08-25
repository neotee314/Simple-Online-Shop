package com.neotee.ecommercesystem.anticorruption;

import com.neotee.ecommercesystem.domainprimitives.OrderId;
import com.neotee.ecommercesystem.domainprimitives.StorageUnitId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.service.DeliveryPackageApplicationService;
import com.neotee.ecommercesystem.usecases.DeliveryPackageUseCases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyFantasticDeliveryPackageUseCaseService implements DeliveryPackageUseCases {

    private final DeliveryPackageApplicationService deliveryPackageApplicationService;

    @Override
    public List<UUID> getContributingStorageUnitsForOrder(UUID orderId) {
        if (orderId == null)
            throw new DomainValidationException("MyFantasticDeliveryPackageUseCaseService", "Order ID darf nicht null sein.");

        var storageUnits = deliveryPackageApplicationService.getContributingStorageUnitsForOrder(OrderId.of(orderId));

        return storageUnits.stream()
                .map(storageUnit -> storageUnit.getId().getId())
                .collect(Collectors.toList());
    }

    @Override
    public Map<UUID, Integer> getDeliveryPackageForOrderAndStorageUnit(UUID orderId, UUID storageUnitId) {
        if (orderId == null)
            throw new DomainValidationException("MyFantasticDeliveryPackageUseCaseService", "Order ID darf nicht null sein.");
        if (storageUnitId == null)
            throw new DomainValidationException("MyFantasticDeliveryPackageUseCaseService", "Storage Unit ID darf nicht null sein.");

        return deliveryPackageApplicationService.getItemsForOrderAndStorageUnitAsUuidMap(
                OrderId.of(orderId),
                StorageUnitId.of(storageUnitId)
        );
    }

    @Override
    @Transactional
    public void deleteAllDeliveryPackages() {
        deliveryPackageApplicationService.deleteAllDeliveryPackages();
    }
}