package com.neotee.ecommercesystem.shopsystem.deliverypackage.application.service;

import com.neotee.ecommercesystem.domainprimitives.OrderId;
import com.neotee.ecommercesystem.domainprimitives.StorageUnitId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.usecases.DeliveryPackageUseCases;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MyFantasticDeliveryPackageUseCaseService
        implements DeliveryPackageUseCases {

    private final DeliveryPackageApplicationService service;

    @Override
    public List<UUID> getContributingStorageUnitsForOrder(UUID orderId) {
        validate(orderId);

        return service
                .getContributingStorageUnitsForOrder(OrderId.of(orderId))
                .stream()
                .map(unit -> unit.getId().getId())
                .toList();
    }

    @Override
    public Map<UUID, Integer> getDeliveryPackageForOrderAndStorageUnit(UUID orderId, UUID storageUnitId) {
        validate(orderId);
        validate(storageUnitId);

        return service.getItemsForOrderAndStorageUnit(
                        OrderId.of(orderId), StorageUnitId.of(storageUnitId)).entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().getId(), Map.Entry::getValue));
    }

    @Override
    public void deleteAllDeliveryPackages() {
        service.deleteAllDeliveryPackages();
    }

    private void validate(UUID id) {
        if (id == null)
            throw new DomainValidationException("MyFantasticDeliveryPackageUseCaseService", "ID cannot be null.");

    }
}