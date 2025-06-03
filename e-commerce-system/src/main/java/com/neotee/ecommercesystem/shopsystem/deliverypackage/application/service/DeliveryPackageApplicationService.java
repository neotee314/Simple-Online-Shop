package com.neotee.ecommercesystem.shopsystem.deliverypackage.application.service;

import com.neotee.ecommercesystem.exception.EntityIdNullException;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.dto.DeliveryPackageDTO;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.mapper.DeliveryPackageMapper;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.DeliveryPackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryPackageApplicationService {
    private final DeliveryPackageUseCaseService deliveryPackageUseCaseService;
    private final DeliveryPackageMapper deliveryPackageMapper;

    public List<DeliveryPackageDTO> getContributingStorageUnitsForOrder(UUID orderId) {
        List<UUID> storageUnitIds = deliveryPackageUseCaseService.getContributingStorageUnitsForOrder(orderId);
        List<DeliveryPackageDTO> contributingStorageUnits = new ArrayList<>();
        for (UUID storageUnitId : storageUnitIds) {
            Map<UUID, Integer> contents = deliveryPackageUseCaseService.getDeliveryPackageForOrderAndStorageUnit(orderId, storageUnitId);
            DeliveryPackageDTO dto = deliveryPackageMapper.mapToDto(orderId, storageUnitId, contents);
            contributingStorageUnits.add(dto);

        }
        return contributingStorageUnits;
    }

    public List<DeliveryPackageDTO> getDeliveryPackageForOrderAndStorageUnit(UUID orderId, UUID storageUnitId) {
        if (orderId == null || storageUnitId == null) throw new EntityIdNullException();

        Map<UUID, Integer> contents = deliveryPackageUseCaseService.getDeliveryPackageForOrderAndStorageUnit(orderId, storageUnitId);
        if (contents == null || contents.isEmpty()) return List.of();

        DeliveryPackageDTO dto = deliveryPackageMapper.mapToDto(orderId, storageUnitId, contents);

        return List.of(dto);
    }


    public void deleteAllDeliveryPackages() {
        deliveryPackageUseCaseService.deleteAllDeliveryPackages();
    }
}
