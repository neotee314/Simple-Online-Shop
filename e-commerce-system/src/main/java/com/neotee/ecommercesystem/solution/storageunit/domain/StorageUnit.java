package com.neotee.ecommercesystem.solution.storageunit.domain;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.HomeAddress;
import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import java.util.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class StorageUnit {
    @Id
    private UUID storageId;

    private String name;
    @Embedded
    private HomeAddress address;

    @ElementCollection
    private Map<UUID, Integer> stockLevels = new HashMap<>();


    public StorageUnit(HomeAddress address, String name) {
        if (address == null || name == null || name.isBlank())
            throw new ShopException("Address and name must not be null or blank");
        this.storageId = UUID.randomUUID();
        this.address = address;
        this.name = name;
    }

    public void addToStock(UUID thingId, Integer quantity) {
        if (thingId == null || quantity == null || quantity < 0) {
            throw new ShopException("Invalid thing ID or quantity must be greater than 0");
        }

        int current = stockLevels.getOrDefault(thingId, 0);
        stockLevels.put(thingId, current + quantity);
    }

    public RemovalPlan planRemoval(UUID thingId, int quantityToRemove, int reservedQuantity) {
        if (thingId == null || quantityToRemove < 0 || reservedQuantity < 0)
            throw new ShopException("Invalid thing ID or quantity to remove must be greater than 0");
        int currentStock = getAvailableStock(thingId);
        int totalAvailable = currentStock + reservedQuantity;

        if (quantityToRemove > totalAvailable) {
            throw new ShopException("The removed quantity exceeds available and reserved stock.");
        }

        int fromStock = Math.min(currentStock, quantityToRemove);
        int fromReserved = quantityToRemove - fromStock;

        return new RemovalPlan(fromStock, fromReserved);
    }



    public void removeFromStock(UUID thingId, Integer removeQuantity) {
        if (thingId == null || removeQuantity == null || removeQuantity <= 0) {
            throw new ShopException("Invalid thing ID or quantity to remove must be greater than 0");
        }

        Integer currentQuantity = stockLevels.get(thingId);
        if (currentQuantity == null) {
            throw new ShopException("Thing not found in stock");
        }

        if (currentQuantity < removeQuantity) {
            throw new ShopException("Not enough quantity in stock to remove");
        }

        int updatedQuantity = currentQuantity - removeQuantity;

        if (updatedQuantity == 0) {
            stockLevels.remove(thingId);
        } else {
            stockLevels.put(thingId, updatedQuantity);
        }
    }

    public boolean canServeAnyOf(Map<UUID, Integer> items) {
        return !getServableItems(items).isEmpty();
    }

    public void changeStockTo(UUID thingId, Integer newTotalQuantity) {
        if (thingId == null || newTotalQuantity == null || newTotalQuantity < 0) {
            throw new ShopException("Invalid thing ID or new quantity must not be negative");
        }

        if (newTotalQuantity == 0) {
            stockLevels.remove(thingId);
        } else {
            stockLevels.put(thingId, newTotalQuantity);
        }
    }

    public Integer getAvailableStock(UUID thingId) {
        if (thingId == null) throw new ShopException("Thing ID must not be null");
        Integer stock = stockLevels.get(thingId);
        return stock == null ? 0 : stock;
    }

    public List<UUID> getTotalContributingItems(Map<UUID, Integer> items) {
        List<UUID> availableItems = new ArrayList<>();
        for (UUID thingId : items.keySet()) {
            if (stockLevels.containsKey(thingId))
                availableItems.add(thingId);
        }
        return availableItems;
    }

    public Integer getTotalCount(Map<UUID, Integer> items) {
        Integer totalCount = 0;
        for (UUID thingId : items.keySet()) {
            if (!stockLevels.containsKey(thingId)) continue;
            totalCount += stockLevels.get(thingId);
        }
        return totalCount;
    }


    public boolean contains(UUID itemId) {
        if (itemId == null) throw new ShopException("Thing ID must not be null");
        return stockLevels.containsKey(itemId);
    }

    public Integer getDistanceToClient(ZipCode clientZipCode) {
        if (clientZipCode == null) throw new ShopException("Client ZIP code must not be null");
        return clientZipCode.difference(address.getZipCode());
    }


    private boolean containsWithQuantity(UUID thingId, int requiredQuantity) {
        if (requiredQuantity < 0) return false;
        if (thingId == null) throw new ShopException("Thing ID must not be null");
        if (!stockLevels.containsKey(thingId)) return false;
        return stockLevels.get(thingId) >= requiredQuantity;
    }

    public Map<UUID, Integer> getServableItems(Map<UUID, Integer> remainingItems) {
        if (remainingItems == null) throw new ShopException("Remaining items must not be null");

        Map<UUID, Integer> canServeItems = new LinkedHashMap<>();

        // Sort entries by descending quantity
        List<Map.Entry<UUID, Integer>> sortedEntries = new ArrayList<>(remainingItems.entrySet());
        sortedEntries.stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEach(sortedEntries::add);

        for (Map.Entry<UUID, Integer> entry : sortedEntries) {
            UUID thingId = entry.getKey();
            int requiredQuantity = entry.getValue();

            if (this.containsWithQuantity(thingId, requiredQuantity)) {
                canServeItems.put(thingId, requiredQuantity);
                if (requiredQuantity >= 10) break;
            }
        }

        return canServeItems;
    }


    public int getAvailableStocks(Map<UUID, Integer> items) {
        if (items == null) throw new ShopException("Items must not be null");
        int availableStocks = 0;
        for (UUID thingId : items.keySet()) {
            int quantity = items.get(thingId);
            if (this.containsWithQuantity(thingId, quantity)) {
                availableStocks += stockLevels.get(thingId);
            }
        }
        return availableStocks;
    }

    public boolean unsufficient(Map<UUID, Integer> items) {
        if (items == null) throw new ShopException("Items must not be null");
        int requiredStocks = 0;
        int availableStocks = 0;
        for (UUID thingId : items.keySet()) {
            int quantity = items.get(thingId);
            requiredStocks += quantity;
            if (this.containsWithQuantity(thingId, quantity)) {
                availableStocks += stockLevels.get(thingId);
            }
        }
        return availableStocks >= requiredStocks;
    }
}
