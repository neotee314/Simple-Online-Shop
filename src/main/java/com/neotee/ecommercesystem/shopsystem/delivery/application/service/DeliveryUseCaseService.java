package com.neotee.ecommercesystem.shopsystem.delivery.application.service;

import com.neotee.ecommercesystem.domainprimitives.DeliveryPackageId;
import com.neotee.ecommercesystem.domainprimitives.DeliveryPackageStatus;
import com.neotee.ecommercesystem.domainprimitives.OrderId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.exceptions.EntityNotFoundException;
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
        if (orderId == null || deliveryRecipient == null)
            throw new EntityNotFoundException("DeliveryUseCaseService", "Order or Delivery not found");
        return deliveryApplicationService.createDelivery(OrderId.of(orderId), deliveryRecipient).getId().getId();
    }

    @Override
    public List<UUID> getDeliveryPackages(UUID deliveryId) {
        return deliveryApplicationService.getDeliveryPackages(deliveryId);
    }

    @Override
    public String getDeliveryPackageStatus(UUID deliveryPackageId) {
        return deliveryApplicationService.getDeliveryPackageStatus(
                DeliveryPackageId.of(deliveryPackageId)
        ).name();
    }

    @Override
    public void updateDeliveryPackageStatus(UUID deliveryPackageId, String status) {
        if (status == null || status.isBlank()) {
            throw new DomainValidationException("DeliveryUseCaseService", "Status cannot be null or empty");
        }

        DeliveryPackageStatus enumStatus;
        try {
            enumStatus = DeliveryPackageStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DomainValidationException("DeliveryUseCaseService", "Invalid status value: " + status + ". Allowed values: NOT_SHIPPED, IN_TRANSIT, DELIVERED");
        }
        deliveryApplicationService.updateDeliveryPackageStatus(
                DeliveryPackageId.of(deliveryPackageId),
                DeliveryPackageStatus.valueOf(status)
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

