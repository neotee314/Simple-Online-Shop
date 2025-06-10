package com.neotee.ecommercesystem.shopsystem.storageunit.application.service;

import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.exception.EntityNotFoundException;
import com.neotee.ecommercesystem.exception.ThingQuantityNotAvailableException;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.*;
import com.neotee.ecommercesystem.shopsystem.thing.application.service.InventoryServiceInterface;
import com.neotee.ecommercesystem.shopsystem.thing.domain.Thing;
import com.neotee.ecommercesystem.shopsystem.thing.domain.ThingId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryFulfillmentService implements InventoryServiceInterface {

    private final StorageUnitRepository storageUnitRepository;
    private final StockLevelRepository stockLevelRepository;

    public StorageUnit findById(UUID storageUnitId) {
        return storageUnitRepository.findById(new StorageUnitId(storageUnitId))
                .orElseThrow(EntityNotFoundException::new);

    }

    public List<StorageUnit> findAll() {
        return new ArrayList<>(storageUnitRepository.findAll());
    }


    public void removeFromStock(StorageUnit storageUnit, Map<Thing, Integer> thingQuantityMap) {
        for (Thing thing : thingQuantityMap.keySet()) {
            storageUnit.removeFromStock(thing.getThingId(), thingQuantityMap.get(thing));
            storageUnitRepository.save(storageUnit);
        }
    }


    @Transactional
    public List<StorageUnitId> getContributingStorageUnit(Map<Thing, Integer> items, ZipCode clientZipCode) {
        Map<Thing, Integer> remainingItems = new HashMap<>(items);
        List<StorageUnitId> contributorMap = new ArrayList<>();
        List<StorageUnit> storageUnits = new ArrayList<>(findAll());
        while (!remainingItems.isEmpty()) {

            List<StorageUnitId> sortedStorageUnitIds = sortStorageUnits(storageUnits, remainingItems, clientZipCode);
            if (sortedStorageUnitIds.isEmpty()) break;

            StorageUnitId storageId = sortedStorageUnitIds.getFirst();
            StorageUnit storageUnit = findById(storageId.getId());
            if (storageUnit == null) break;

            Map<Thing, Integer> servableItems = storageUnit.getServableItems(remainingItems);
            if (!servableItems.isEmpty()) {
                servableItems.keySet().forEach(remainingItems::remove);
                contributorMap.add(storageId);
            }
            storageUnits.remove(storageUnit);

        }

        return contributorMap;
    }

    public List<StorageUnitId> sortStorageUnits(List<StorageUnit> storageUnits,
                                                Map<Thing, Integer> unfulfilledItems,
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
                .anyMatch(unit -> unit.contains(new ThingId(thingId)));
    }

    @Override
    public void deleteAllStockLevel() {
        stockLevelRepository.deleteAll();
        log.info("Deleted all stock levels");

    }
}



