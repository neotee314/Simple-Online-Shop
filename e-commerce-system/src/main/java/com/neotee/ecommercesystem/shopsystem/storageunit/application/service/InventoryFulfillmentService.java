package com.neotee.ecommercesystem.shopsystem.storageunit.application.service;

import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.exception.EntityNotFoundException;
import com.neotee.ecommercesystem.exception.ThingQuantityNotAvailableException;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StockLevelRepository;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnit;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnitId;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnitRepository;
import com.neotee.ecommercesystem.shopsystem.thing.application.service.InventoryServiceInterface;
import com.neotee.ecommercesystem.shopsystem.thing.domain.Thing;
import com.neotee.ecommercesystem.shopsystem.thing.domain.ThingId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

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


    @Transactional
    public void removeFromStock(Map<Thing, Integer> thingQuantityMap) {
        List<StorageUnit> storageUnits = findAll();

        thingQuantityMap.forEach((thing, requiredQty) -> {
            StorageUnit unit = storageUnits.stream()
                    .filter(su -> su.hasSufficientQuantity(thing, requiredQty))
                    .findFirst()
                    .orElseThrow(ThingQuantityNotAvailableException::new);

            unit.removeFromStock(thing.getThingId(), requiredQty);
            storageUnitRepository.save(unit);
        });
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
            if (storageUnit == null) continue;

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

        // Step 1: Define comparators
        Comparator<StorageUnit> byContributingItems = Comparator
                .comparingInt((StorageUnit su) -> su.getTotalContributingItems(unfulfilledItems))
                .reversed();

        Comparator<StorageUnit> byAvailableStock = Comparator
                .comparingInt((StorageUnit su) -> su.getAvailableStocks(unfulfilledItems))
                .reversed();

        Comparator<StorageUnit> byDistance = Comparator
                .comparingInt(su -> su.getDistanceToClient(clientZipCode));

        // Step 2: Check if any storage unit can fulfill all items
        List<StorageUnit> fullCoverageUnits = storageUnits.stream()
                .filter(su -> su.areItemsFulfilled(unfulfilledItems))
                .toList();

        if (!fullCoverageUnits.isEmpty()) {
            // If at least one unit can fulfill all items, sort them only by distance (closer is better)
            return fullCoverageUnits.stream()
                    .sorted(byDistance)
                    .map(StorageUnit::getStorageId)
                    .toList();
        }

        // Step 3: Otherwise, sort all units based on:
        // 1. Contributing items count (more is better)
        // 2. Available stock for those items (more is better)
        // 3. Distance to client (less is better)
        return storageUnits.stream()
                .sorted(byContributingItems
                        .thenComparing(byAvailableStock)
                        .thenComparing(byDistance))
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



