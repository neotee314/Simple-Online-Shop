package com.neotee.ecommercesystem.shopsystem.deliverypackage.application.service;

import com.neotee.ecommercesystem.domainprimitives.DeliveryPackageId;
import com.neotee.ecommercesystem.domainprimitives.OrderId;
import com.neotee.ecommercesystem.domainprimitives.StorageUnitId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.port.out.FindOrderPort;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.port.out.FindStorageUnitsPort;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.port.out.UpdateStorageUnitPort;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.DeliveryPackage;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.DeliveryPackageRepository;
import com.neotee.ecommercesystem.shopsystem.order.domain.Order;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StockLevel;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        var order = findOrderPort.findById(orderId);
        if (order == null) {
            throw new DomainValidationException("Delivery", "Order nicht gefunden.");
        }

        var items = order.getOrderLineItemsMap();
        if (items.isEmpty()) {
            throw new DomainValidationException("Delivery", "Order hat keine Artikel.");
        }

        var clientZipCode = order.getClient().getHomeAddress().getZipCode();
        var allStorageUnits = findStorageUnitsPort.findAll();

        var contributingStorageUnits = new ArrayList<StorageUnit>();

        for (var storageUnit : allStorageUnits) {
            // ✅ بررسی کن که StorageUnit میتونه کل سفارش رو تأمین کنه
            boolean canFulfillAll = true;
            for (var entry : items.entrySet()) {
                var product = entry.getKey();
                var quantity = entry.getValue();
                if (!storageUnit.hasSufficientQuantityOf(product, quantity)) {
                    canFulfillAll = false;
                    break;
                }
            }

            if (canFulfillAll) {
                contributingStorageUnits.add(storageUnit);
            }
        }

        // مرتب‌سازی بر اساس نزدیک‌ترین فاصله
        contributingStorageUnits.sort(Comparator.comparingInt(su -> su.getDistanceToClient(clientZipCode)));

        return contributingStorageUnits;
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

        // ✅ مرتب‌سازی بر اساس نزدیک‌ترین فاصله
        storageUnits.sort(Comparator.comparingInt(su -> su.getDistanceToClient(clientZipCode)));

        for (var storageUnit : storageUnits) {
            if (remainingItems.isEmpty()) break;

            // ✅ فقط محصولاتی که موجودی کافی دارن رو بردار
            var servableItems = storageUnit.getServableItems(remainingItems);
            if (servableItems.isEmpty()) continue;

            var deliveryPackage = DeliveryPackage.create(storageUnit, order);
            deliveryPackage.addParts(servableItems);

            for (var entry : servableItems.entrySet()) {
                var product = entry.getKey();
                var quantity = entry.getValue();
                storageUnit.removeFromStock(product.getId(), quantity);
            }
            updateStorageUnitPort.update(storageUnit);

            var savedPackage = deliveryPackageRepository.save(deliveryPackage);
            deliveryPackages.add(savedPackage);

            // ✅ محصولات تأمین شده رو از لیست حذف کن
            for (var product : servableItems.keySet()) {
                remainingItems.remove(product);
            }
        }

        if (deliveryPackages.isEmpty())
            throw new RuntimeException("Keine Storage Unit konnte die Bestellung erfüllen.");

        return deliveryPackages;
    }
    public void deleteAllDeliveryPackages() {
        deliveryPackageRepository.deleteAll();
    }

    public Map<UUID, Integer> getItemsForOrderAndStorageUnitAsUuidMap(OrderId orderId, StorageUnitId storageUnitId) {
        var order = findOrderPort.findById(orderId);
        findStorageUnitsPort.findById(storageUnitId);
        var deliveryPackage = findByOrderIdAndStorageUnitId(orderId, storageUnitId);
        return deliveryPackage.getItemsAsUuidMap();
    }
}