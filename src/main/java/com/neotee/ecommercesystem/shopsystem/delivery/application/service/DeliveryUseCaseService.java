package com.neotee.ecommercesystem.shopsystem.delivery.application.service;

import com.neotee.ecommercesystem.domainprimitives.DeliveryPackageStatus;
import com.neotee.ecommercesystem.domainprimitives.OrderId;
import com.neotee.ecommercesystem.usecases.ClientType;
import com.neotee.ecommercesystem.usecases.DeliveryUseCases;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DeliveryUseCaseService implements DeliveryUseCases {

    private final DeliveryApplicationService deliveryApplicationService;

    public DeliveryUseCaseService(DeliveryApplicationService deliveryApplicationService) {
        this.deliveryApplicationService = deliveryApplicationService;
    }

    @Override
    public UUID triggerDelivery(UUID orderId, ClientType deliveryRecipient) {
        return deliveryApplicationService.createDelivery(OrderId.of(orderId), deliveryRecipient).getId().getId();
    }

    @Override
    public List<UUID> getDeliveryPackages(UUID deliveryId) {
        return deliveryApplicationService.getDeliveryPackages(deliveryId);
    }

    @Override
    public DeliveryPackageStatus getDeliveryPackageStatus(UUID deliveryPackageId) {
        return deliveryApplicationService.getDeliveryPackageStatus(
                com.neotee.ecommercesystem.domainprimitives.DeliveryPackageId.of(deliveryPackageId)
        );
    }

    @Override
    public void updateDeliveryPackageStatus(UUID deliveryPackageId, DeliveryPackageStatus status) {
        deliveryApplicationService.updateDeliveryPackageStatus(
                com.neotee.ecommercesystem.domainprimitives.DeliveryPackageId.of(deliveryPackageId),
                status
        );
    }

    @Override
    public List<UUID> getDeliveryHistory(EmailType clientEmail) {
        return deliveryApplicationService.getDeliveryHistory(clientEmail);
    }

    @Override
    public void deleteAllDeliveries() {
        deliveryApplicationService.deleteAllDeliveries();
    }
}

