package com.neotee.ecommercesystem.solution.storageunit.application;

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
public class StorageUnitService {

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
        Map<UUID, Boolean> contributorMap = new LinkedHashMap<>();

        while (!remainingItems.isEmpty()) {
            List<StorageUnit> storageUnits = new ArrayList<>(findAll());
            List<UUID> sortedStorageUnitIds = sortStorageUnits(storageUnits, remainingItems, clientZipCode);
            if (sortedStorageUnitIds.isEmpty()) break;

            UUID storageId = sortedStorageUnitIds.getFirst();
            StorageUnit unit = findById(storageId);
            if (unit == null) continue;

            Map<UUID, Integer> servableItems = unit.getServableItems(remainingItems);
            if (servableItems.isEmpty()) {
                contributorMap.putIfAbsent(storageId, false);
            } else {
                servableItems.keySet().forEach(remainingItems::remove);
                contributorMap.put(storageId, true);
            }

            storageUnits.removeIf(su -> su.getStorageId().equals(storageId));
        }

        return contributorMap.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .toList();
    }


    @Transactional
    public List<UUID> sortStorageUnits(List<StorageUnit> storageUnits, Map<UUID, Integer> unfulfilledItems, ZipCode clientZipCode)
    {
        int totalquantity = unfulfilledItems.values().stream().mapToInt(Integer::intValue).sum();

        storageUnits.sort((su1, su2) -> {
            int compare = Integer.compare(su2.getTotalContributingItems(unfulfilledItems).size(),
                    su1.getTotalContributingItems(unfulfilledItems).size());

            if (compare == 0 || su2.getDistanceToClient(clientZipCode) < su1.getDistanceToClient(clientZipCode)) {
                compare = Integer.compare(su1.getDistanceToClient(clientZipCode),
                        su2.getDistanceToClient(clientZipCode));
                if (compare == 0 ||
                        su2.getAvailableStocks(unfulfilledItems) < totalquantity||
                        su1.getAvailableStocks(unfulfilledItems) < totalquantity
                )     {
                    return Integer.compare(su2.getAvailableStocks(unfulfilledItems),
                            su1.getAvailableStocks(unfulfilledItems));
                }

            }
            return compare;
        });
        return storageUnits.stream().map(StorageUnit::getStorageId).toList();
    }

    }



