package com.neotee.ecommercesystem.solution.storageunit.application;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.solution.order.application.OrderService;
import com.neotee.ecommercesystem.solution.order.domain.Order;
import com.neotee.ecommercesystem.solution.storageunit.domain.StorageUnit;
import com.neotee.ecommercesystem.solution.storageunit.domain.StorageUnitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageUnitService {

    private final StorageUnitRepository storageUnitRepository;
    private final OrderService orderService;


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
    public List<UUID> getContributingStorageUnit(UUID orderId) {
        Map<UUID, Integer> remainingItems = new HashMap<>(orderService.getOrderPartWithQuantity(orderId));
        ZipCode clientZipCode = orderService.findClientZipCode(orderId);

        Order order = orderService.findById(orderId);
        if (order == null) throw new ShopException("Order does not exist");
        Map<UUID, Boolean> contributorMap = new LinkedHashMap<>();


        while (!remainingItems.isEmpty()) {
            List<StorageUnit> storageUnits = new ArrayList<>(findAll());
            List<UUID> sortedStorageUnitIds = sortStorageUnits(storageUnits, remainingItems, clientZipCode,order);
            if (sortedStorageUnitIds.isEmpty()) break;

            UUID storageId = sortedStorageUnitIds.getFirst();
            StorageUnit unit = findById(storageId);

            if (unit == null) continue;

            Map<UUID, Integer> servableItems = unit.getServableItems(remainingItems);
            if (!servableItems.isEmpty()) {
                servableItems.keySet().forEach(remainingItems::remove);
                contributorMap.put(storageId, true); // this unit contributed
            } else {
                contributorMap.putIfAbsent(storageId, false); // track as non-contributing for now
            }

            storageUnits.removeIf(su -> su.getStorageId().equals(storageId));
        }

        // only return contributors who actually delivered something
        return contributorMap.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .toList();
    }


    @Transactional
    public List<UUID> sortStorageUnits(List<StorageUnit> storageUnits, Map<UUID, Integer> unfulfilledItems, ZipCode clientZipCode
    ,Order order) {

        storageUnits.sort((su1, su2) -> {
            int compareItemCount = Integer.compare(su2.getTotalContributingItems(unfulfilledItems).size(),
                    su1.getTotalContributingItems(unfulfilledItems).size());
            if (compareItemCount == 0 || su2.getDistanceToClient(clientZipCode) < su1.getDistanceToClient(clientZipCode)) {
                compareItemCount = Integer.compare(su1.getDistanceToClient(clientZipCode),
                        su2.getDistanceToClient(clientZipCode));
                if (compareItemCount == 0 ||
                        su2.getAvailableStocks(unfulfilledItems) < order.getQuantity() ||
                        su1.getAvailableStocks(unfulfilledItems) < order.getQuantity()
                )     {
                    return Integer.compare(su2.getAvailableStocks(unfulfilledItems),
                            su1.getAvailableStocks(unfulfilledItems));
                }

            }
            return compareItemCount;
        });
        return storageUnits.stream().map(StorageUnit::getStorageId).toList();
    }

    }



