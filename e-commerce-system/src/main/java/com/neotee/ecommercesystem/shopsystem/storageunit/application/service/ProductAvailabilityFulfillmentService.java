package com.neotee.ecommercesystem.shopsystem.storageunit.application.service;

import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.exception.EntityNotFoundException;
import com.neotee.ecommercesystem.exception.ThingQuantityNotAvailableException;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.*;
import com.neotee.ecommercesystem.shopsystem.product.application.port.out.ProductAvailabilityPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductAvailabilityFulfillmentService implements ProductAvailabilityPort {

    private final StorageUnitRepository storageUnitRepository;
    private final StockLevelRepository stockLevelRepository;

    public StorageUnit findById(UUID storageUnitId) {
        return storageUnitRepository.findById(new StorageUnitId(storageUnitId))
                .orElseThrow(EntityNotFoundException::new);

    }

    public List<StorageUnit> findAll() {
        return new ArrayList<>(storageUnitRepository.findAll());
    }


    public void removeFromStock(StorageUnit storageUnit, Map<Product, Integer> thingQuantityMap) {
        for (Product product : thingQuantityMap.keySet()) {
            storageUnit.removeFromStock(product.getProductId(), thingQuantityMap.get(product));
            storageUnitRepository.save(storageUnit);
        }
    }


    @Transactional
    public List<StorageUnitId> getContributingStorageUnit(Map<Product, Integer> items, ZipCode clientZipCode) {
        Map<Product, Integer> remainingItems = new HashMap<>(items);
        List<StorageUnitId> contributorMap = new ArrayList<>();
        List<StorageUnit> storageUnits = new ArrayList<>(findAll());
        while (!remainingItems.isEmpty()) {

            List<StorageUnitId> sortedStorageUnitIds = sortStorageUnits(storageUnits, remainingItems, clientZipCode);
            if (sortedStorageUnitIds.isEmpty()) break;

            StorageUnitId storageId = sortedStorageUnitIds.getFirst();
            StorageUnit storageUnit = findById(storageId.getId());
            if (storageUnit == null) break;

            Map<Product, Integer> servableItems = storageUnit.getServableItems(remainingItems);
            if (!servableItems.isEmpty()) {
                servableItems.keySet().forEach(remainingItems::remove);
                contributorMap.add(storageId);
            }
            storageUnits.remove(storageUnit);

        }

        return contributorMap;
    }

    public List<StorageUnitId> sortStorageUnits(List<StorageUnit> storageUnits,
                                                Map<Product, Integer> unfulfilledItems,
                                                ZipCode clientZipCode) {
        return storageUnits.stream()
                .sorted(new StorageUnitComparator(unfulfilledItems, clientZipCode))
                .map(StorageUnit::getStorageId)
                .toList();
    }


    public int getAvailableInventory(UUID thingId) {
        List<StorageUnit> storageUnits = findAll();
        return storageUnits.stream()
                .mapToInt(unit -> unit.getAvailableStock(thingId))
                .sum();
    }

    @Override
    public Boolean isInStock(UUID thingId) {
        List<StorageUnit> storageUnits = findAll();
        return storageUnits.stream()
                .anyMatch(unit -> unit.contains(new ProductId(thingId)));
    }

    @Override
    public void deleteAllStockLevel() {
        stockLevelRepository.deleteAll();
        log.info("Deleted all stock levels");

    }
}



