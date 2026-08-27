package com.neotee.ecommercesystem.shopsystem.deliverypackage.application.service;

import com.neotee.ecommercesystem.domainprimitives.OrderId;
import com.neotee.ecommercesystem.domainprimitives.StorageUnitId;
import com.neotee.ecommercesystem.exceptions.EntityNotFoundException;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.port.out.FindOrderPort;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.port.out.FindStorageUnitsPort;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.port.out.UpdateStorageUnitPort;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.DeliveryPackage;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.DeliveryPackageRepository;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.service.DeliveryPackageDomainService;
import com.neotee.ecommercesystem.shopsystem.order.domain.Order;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DeliveryPackageApplicationService {

    private final DeliveryPackageRepository deliveryPackageRepository;
    private final FindOrderPort findOrderPort;
    private final FindStorageUnitsPort findStorageUnitsPort;
    private final UpdateStorageUnitPort updateStorageUnitPort;
    private final DeliveryPackageDomainService domainService;

    public List<DeliveryPackage> createDeliveryPackages(
            OrderId orderId
    ) {
        var order = findOrder(orderId);
        var storageUnits = findStorageUnitsPort.findAll();

        System.out.println("\n================ DELIVERY PACKAGE CREATION DEBUG ================");
        System.out.println("Order ID: " + orderId.getId());

        System.out.println("\nStorageUnits received from FindStorageUnitsPort:");

        for (int i = 0; i < storageUnits.size(); i++) {
            StorageUnit storageUnit = storageUnits.get(i);

            System.out.println(
                    "StorageUnit[" + i + "]"
                            + " ID=" + storageUnit.getId().getId()
            );

            System.out.println(
                    "  StorageUnit object=" + storageUnit
            );
        }

        System.out.println("\nCalling domainService.createPackages(...)");

        var packages =
                domainService.createPackages(
                        order,
                        storageUnits
                );

        System.out.println("\nPackages created by DomainService:");

        for (DeliveryPackage pkg : packages) {

            System.out.println(
                    "DeliveryPackage ID=" + pkg.getId().getId()
            );

            System.out.println(
                    "  StorageUnit ID="
                            + pkg.getStorageUnit().getId().getId()
            );

            System.out.println(
                    "  Products:"
            );

            pkg.getItemsAsUuidMap().forEach((productId, quantity) ->
                    System.out.println(
                            "    Product ID=" + productId
                                    + " quantity=" + quantity
                    )
            );
        }

        System.out.println("===============================================================\n");

        savePackages(packages);

        return packages;
    }

    public Map<UUID, Integer> getItemsForOrderAndStorageUnitAsUuidMap(
            OrderId orderId,
            StorageUnitId storageUnitId
    ) {
        return deliveryPackageRepository.findByOrderId(orderId)
                .stream()
                .filter(p -> p.getStorageUnit().getId().equals(storageUnitId))
                .findFirst()
                .map(DeliveryPackage::getItemsAsUuidMap)
                .orElseThrow(() -> new EntityNotFoundException(
                        "DeliveryPackageApplicationService",
                        "Lieferpaket nicht gefunden."
                ));
    }
    private Order findOrder(OrderId orderId) {
        var order = findOrderPort.findById(orderId);

        if (order == null) {
            throw new EntityNotFoundException(
                    "DeliveryPackageApplicationService",
                    "Bestellung nicht gefunden."
            );
        }

        return order;
    }

    private void savePackages(
            List<DeliveryPackage> packages
    ) {
        packages.forEach(packageItem -> {
            updateStorageUnitPort.update(
                    packageItem.getStorageUnit()
            );
            deliveryPackageRepository.save(packageItem);
        });
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
    public List<DeliveryPackage> findByOrderId(
            OrderId orderId
    ) {
        return deliveryPackageRepository.findByOrderId(orderId);
    }

    public DeliveryPackage findByOrderIdAndStorageUnitId(
            OrderId orderId,
            StorageUnitId storageUnitId
    ) {
        return deliveryPackageRepository
                .findByOrderIdAndStorageUnitId(
                        orderId,
                        storageUnitId
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "DeliveryPackageApplicationService",
                                "Lieferpaket nicht gefunden."
                        )
                );
    }

    public void deleteAllDeliveryPackages() {
        deliveryPackageRepository.deleteAll();
    }
}