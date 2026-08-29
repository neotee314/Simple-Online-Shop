package com.neotee.ecommercesystem.shopsystem.delivery.application.service;

import com.neotee.ecommercesystem.domainprimitives.DeliveryId;
import com.neotee.ecommercesystem.domainprimitives.DeliveryPackageId;
import com.neotee.ecommercesystem.domainprimitives.DeliveryPackageStatus;
import com.neotee.ecommercesystem.domainprimitives.OrderId;
import com.neotee.ecommercesystem.exceptions.EntityNotFoundException;
import com.neotee.ecommercesystem.usecases.ClientType;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import com.neotee.ecommercesystem.shopsystem.delivery.domain.model.Delivery;
import com.neotee.ecommercesystem.shopsystem.delivery.domain.repository.DeliveryRepository;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.service.DeliveryPackageApplicationService;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.model.DeliveryPackage;
import com.neotee.ecommercesystem.shopsystem.order.application.service.OrderApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryApplicationService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryPackageApplicationService deliveryPackageApplicationService;
    private final OrderApplicationService orderApplicationService;

    public Delivery createDelivery(OrderId orderId, ClientType deliveryRecipient) {
        var order = orderApplicationService.findById(orderId);
        var deliveryPackageIds = deliveryPackageApplicationService.getDeliveryPackagesForOrder(orderId);

        var delivery = Delivery.create(order, deliveryRecipient);

        deliveryPackageIds.stream()
                .map(deliveryPackageApplicationService::findById)
                .forEach(delivery::addPackage);

        return deliveryRepository.save(delivery);
    }

    public List<UUID> getDeliveryPackages(UUID deliveryId) {
        var delivery = findById(DeliveryId.of(deliveryId));

        return delivery.getDeliveryPackages().stream()
                .map(DeliveryPackage::getId)
                .map(DeliveryPackageId::getId)
                .toList();
    }

    public DeliveryPackageStatus getDeliveryPackageStatus(DeliveryPackageId deliveryPackageId) {
        return deliveryPackageApplicationService.getStatus(deliveryPackageId);
    }

    public void updateDeliveryPackageStatus(DeliveryPackageId deliveryPackageId, DeliveryPackageStatus status) {
        deliveryPackageApplicationService.updateStatus(deliveryPackageId, status);
    }

    public List<UUID> getDeliveryHistory(EmailType clientEmail) {
        return deliveryRepository.findByDeliveryRecipientEmail(clientEmail).stream()
                .map(Delivery::getId)
                .map(DeliveryId::getId)
                .toList();
    }

    public void deleteAllDeliveries() {
        deliveryRepository.deleteAll();
    }

    public Delivery findById(DeliveryId deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "DeliveryApplicationService",
                        "Delivery nicht gefunden."
                ));
    }
}
