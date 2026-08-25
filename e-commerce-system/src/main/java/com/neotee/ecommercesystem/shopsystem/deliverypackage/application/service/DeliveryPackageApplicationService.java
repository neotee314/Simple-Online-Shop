package com.neotee.ecommercesystem.shopsystem.deliverypackage.application.service;

import com.neotee.ecommercesystem.domainprimitives.DeliveryPackageId;
import com.neotee.ecommercesystem.domainprimitives.OrderId;
import com.neotee.ecommercesystem.domainprimitives.StorageUnitId;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.port.out.FindOrderPort;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.port.out.FindStorageUnitsPort;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.port.out.UpdateStorageUnitPort;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.DeliveryPackage;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.DeliveryPackagePart;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.DeliveryPackageRepository;
import com.neotee.ecommercesystem.shopsystem.order.domain.Order;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StockLevel;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryPackageApplicationService {

    private final DeliveryPackageRepository deliveryPackageRepository;
    private final FindOrderPort findOrderPort;
    private final FindStorageUnitsPort findStorageUnitsPort;
    private final UpdateStorageUnitPort updateStorageUnitPort;

    public DeliveryPackage findById(DeliveryPackageId packageId) {
        return deliveryPackageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Lieferpaket nicht gefunden."));
    }

    public List<DeliveryPackage> findByOrderId(OrderId orderId) {
        return deliveryPackageRepository.findByOrderId(orderId);
    }

    public DeliveryPackage findByOrderIdAndStorageUnitId(OrderId orderId, StorageUnitId storageUnitId) {
        return deliveryPackageRepository.findByOrderIdAndStorageUnitId(orderId, storageUnitId)
                .orElseThrow(() -> new RuntimeException("Lieferpaket nicht gefunden."));
    }

    public List<StorageUnit> getContributingStorageUnitsForOrder(OrderId orderId) {
        var storageUnitIds = deliveryPackageRepository.findByOrderId(orderId).stream()
                .map(DeliveryPackage::getStorageUnitId)
                .distinct()
                .collect(Collectors.toList());

        return storageUnitIds.stream()
                .map(findStorageUnitsPort::findById)
                .collect(Collectors.toList());
    }

    public List<StockLevel> getItemsForOrderAndStorageUnit(OrderId orderId, StorageUnitId storageUnitId) {
        var deliveryPackage = deliveryPackageRepository.findByOrderIdAndStorageUnitId(orderId, storageUnitId)
                .orElseThrow(() -> new RuntimeException("Lieferpaket nicht gefunden."));

        return deliveryPackage.getParts().stream()
                .map(part ->StockLevel.create(part.getProduct(), part.getQuantity()))
                .collect(Collectors.toList());
    }

    public List<DeliveryPackage> createDeliveryPackages(OrderId orderId) {
        var order = findOrderPort.findById(orderId);
        if (order == null)
            throw new RuntimeException("Order nicht gefunden.");

        var items = order.getOrderLineItemsMap();
        if (items.isEmpty())
            throw new RuntimeException("Order hat keine Artikel.");

        var clientZipCode = order.getClient().getHomeAddress().getZipCode();
        var storageUnits = findStorageUnitsPort.findAll();
        var deliveryPackages = new ArrayList<DeliveryPackage>();
        var remainingItems = new HashMap<>(items);

        storageUnits.sort(Comparator.comparingInt(su -> su.getDistanceToClient(clientZipCode)));

        for (var storageUnit : storageUnits) {
            if (remainingItems.isEmpty()) break;

            var servableItems = storageUnit.getServableItems(remainingItems);
            if (servableItems.isEmpty()) continue;

            var deliveryPackage = DeliveryPackage.create(storageUnit, order);
            deliveryPackage.addParts(servableItems);

            for (var entry : servableItems.entrySet()) {
                storageUnit.removeFromStock(entry.getKey().getId(), entry.getValue());
            }
            updateStorageUnitPort.update(storageUnit);

            var savedPackage = deliveryPackageRepository.save(deliveryPackage);
            deliveryPackages.add(savedPackage);

            servableItems.keySet().forEach(remainingItems::remove);
        }

        if (deliveryPackages.isEmpty())
            throw new RuntimeException("Keine Storage Unit konnte die Bestellung erfüllen.");

        return deliveryPackages;
    }

    public DeliveryPackage createDeliveryPackage(Order order, StorageUnit storageUnit, Map<Product, Integer> items) {
        var deliveryPackage = DeliveryPackage.create(storageUnit, order);
        deliveryPackage.addParts(items);
        return deliveryPackageRepository.save(deliveryPackage);
    }



    public Map<Product, Integer> getItemsForOrderAndStorageUnitAsProductMap(OrderId orderId, StorageUnitId storageUnitId) {
        var deliveryPackage = findByOrderIdAndStorageUnitId(orderId, storageUnitId);
        return deliveryPackage.getItemsAsProductMap();
    }

    public Map<UUID, Integer> getItemsForOrderAndStorageUnitAsUuidMap(OrderId orderId, StorageUnitId storageUnitId) {
        var deliveryPackage = findByOrderIdAndStorageUnitId(orderId, storageUnitId);
        return deliveryPackage.getItemsAsUuidMap();
    }

    public void deleteAllDeliveryPackages() {
        deliveryPackageRepository.deleteAll();
    }
}