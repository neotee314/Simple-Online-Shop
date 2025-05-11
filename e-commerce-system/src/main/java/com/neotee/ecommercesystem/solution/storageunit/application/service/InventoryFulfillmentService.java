package com.neotee.ecommercesystem.solution.storageunit.application.service;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.solution.storageunit.domain.StorageUnit;
import com.neotee.ecommercesystem.solution.storageunit.domain.StorageUnitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryFulfillmentService {

    private final StorageUnitRepository storageUnitRepository;

    public StorageUnit findById(UUID storageUnitId) {
        Optional<StorageUnit> storageUnitOptional = storageUnitRepository.findById(storageUnitId);

        if (storageUnitOptional.isEmpty())
            throw new ShopException("Storage with Id " + storageUnitId + " does not exist");
        return storageUnitOptional.get();

    }

    public List<StorageUnit> findAll() {
        List<StorageUnit> storageUnits = new ArrayList<>();
        storageUnitRepository.findAll().forEach(storageUnits::add);
        return storageUnits;
    }


    @Transactional
    public void removeFromStock(Map<UUID, Integer> partsWithQuantity) {
        List<StorageUnit> storageUnits = findAll();

        partsWithQuantity.forEach((thingId, requiredQty) -> {
            StorageUnit unit = storageUnits.stream()
                    .filter(su -> su.getAvailableStock(thingId) >= requiredQty)
                    .findFirst()
                    .orElseThrow(() -> new ShopException("Not enough stock for thing ID: " + thingId));

            unit.removeFromStock(thingId, requiredQty);
            storageUnitRepository.save(unit);
        });
    }


    @Transactional
    public List<UUID> getContributingStorageUnit(Map<UUID, Integer> items, ZipCode clientZipCode) {
        Map<UUID, Integer> remainingItems = new HashMap<>(items);
        List<UUID> contributorMap = new ArrayList<>();
        List<StorageUnit> storageUnits = new ArrayList<>(findAll());
        while (!remainingItems.isEmpty()) {

            List<UUID> sortedStorageUnitIds = sortStorageUnits(storageUnits, remainingItems, clientZipCode);
            if (sortedStorageUnitIds.isEmpty()) break;

            UUID storageId = sortedStorageUnitIds.getFirst();
            StorageUnit storageUnit = findById(storageId);
            if (storageUnit == null) continue;

            Map<UUID, Integer> servableItems = storageUnit.getServableItems(remainingItems);
            if (!servableItems.isEmpty()) {
                servableItems.keySet().forEach(remainingItems::remove);
                contributorMap.add(storageId);
            }
            storageUnits.remove(storageUnit);

        }

        return contributorMap;
    }

    public List<UUID> sortStorageUnits(List<StorageUnit> storageUnits,
                                       Map<UUID, Integer> unfulfilledItems,
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
                .filter(su -> su.canFullfillAll(unfulfilledItems))
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

}



