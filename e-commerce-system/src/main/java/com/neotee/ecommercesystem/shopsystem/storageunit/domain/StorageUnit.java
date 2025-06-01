package com.neotee.ecommercesystem.shopsystem.storageunit.domain;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.HomeAddress;
import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.exception.*;
import com.neotee.ecommercesystem.exception.EntityNotFoundException;
import com.neotee.ecommercesystem.shopsystem.thing.domain.Thing;
import com.neotee.ecommercesystem.shopsystem.thing.domain.ThingId;
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
    private StorageUnitId storageId;

    private String name;
    @Embedded
    private HomeAddress address;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "storage_unit_id")
    private List<StockLevel> stockLevels = new ArrayList<>();


    public StorageUnit(HomeAddress address, String name) {
        if (address == null || name == null || name.isBlank())
            throw new ValueObjectNullOrEmptyException();
        this.storageId = new StorageUnitId();
        this.address = address;
        this.name = name;
    }

    public void addToStock(Thing thing, Integer quantity) {
        if (thing == null) throw new EntityNotFoundException();
        if (quantity == null) throw new ValueObjectNullOrEmptyException();
        if (quantity < 0) throw new QuantityNegativeException();

        for (StockLevel stockLevel : stockLevels) {
            if (stockLevel.contains(thing))
                stockLevel.addToQuantity(quantity);
        }
        StockLevel stockLevel = new StockLevel(thing, quantity);
        stockLevels.add(stockLevel);
    }


    public void removeFromStock(ThingId thingId, Integer removeQuantity) {
        if (thingId == null) throw new EntityIdNullException();

        for (StockLevel stockLevel : stockLevels) {
            if (stockLevel.contains(thingId)) {
                stockLevel.removeFromQuantity(removeQuantity);
                if (stockLevel.getQuantityInStock() <= 0) {
                    stockLevels.remove(stockLevel);
                }
                return;
            }
        }
    }


    public void changeStockTo(Thing thing, Integer newTotalQuantity) {
        if (thing == null) throw new EntityNotFoundException();
        for (StockLevel stockLevel : stockLevels) {
            if (stockLevel.contains(thing)) {
                stockLevel.changeStockTo(newTotalQuantity);
                if (stockLevel.getQuantityInStock() <= 0) {
                    stockLevels.remove(stockLevel);
                }
                return;
            }
        }
    }

    public Integer getAvailableStock(UUID thingId) {
        if (thingId == null) throw new EntityIdNullException();
        for (StockLevel stockLevel : stockLevels) {
            if (stockLevel.contains(new ThingId(thingId))) {
                return stockLevel.getQuantityInStock();
            }
        }
        return 0;
    }

    public Integer getTotalContributingItems(Map<Thing, Integer> items) {
        Integer totalContributingItems = 0;

        for (Thing thing : items.keySet()) {
            for (StockLevel stockLevel : stockLevels) {
                if (stockLevel.contains(thing))
                    totalContributingItems += 1;
            }

        }
        return totalContributingItems;
    }


    public boolean contains(ThingId thingId) {
        if (thingId == null) throw new EntityIdNullException();
        for (StockLevel stockLevel : stockLevels) {
            if (stockLevel.contains(thingId)) {
                return true;
            }
        }
        return false;
    }

    public Integer getDistanceToClient(ZipCode clientZipCode) {
        if (clientZipCode == null) throw new ValueObjectNullOrEmptyException();
        return clientZipCode.difference(address.getZipCode());
    }


    public boolean hasSufficientQuantity(Thing thing, int requiredQuantity) {
        if (requiredQuantity < 0) return false;
        if (thing == null) throw new EntityNotFoundException();
        if (!contains(thing.getThingId())) return false;
        return getQuantityOf(thing) >= requiredQuantity;
    }

    public Map<Thing, Integer> getServableItems(Map<Thing, Integer> remainingItems) {
        if (remainingItems == null) throw new ShopException("Remaining items must not be null");
        Map<Thing, Integer> canServeItems = new LinkedHashMap<>();

        // Sort entries by descending quantity
        List<Map.Entry<Thing, Integer>> sortedEntries = new ArrayList<>(remainingItems.entrySet());
        sortedEntries.stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEach(sortedEntries::add);

        for (Map.Entry<Thing, Integer> entry : sortedEntries) {
            Thing thing = entry.getKey();
            int requiredQuantity = entry.getValue();

            if (this.hasSufficientQuantity(thing, requiredQuantity)) {
                canServeItems.put(thing, requiredQuantity);
                if (requiredQuantity >= 10) break;
            }
        }

        return canServeItems;
    }


    public int getAvailableStocks(Map<Thing, Integer> items) {
        if (items == null) throw new ShopException("Items must not be null");
        int availableStocks = 0;
        for (Thing thing : items.keySet()) {
            int quantity = items.get(thing);
            if (this.hasSufficientQuantity(thing, quantity)) {
                availableStocks += getQuantityOf(thing);
            }
        }
        return availableStocks;
    }


    public boolean areItemsFulfilled(Map<Thing, Integer> unfulfilledItems) {
        if (unfulfilledItems == null) throw new ShopException("Unfulfilled items must not be null");

        for (Map.Entry<Thing, Integer> entry : unfulfilledItems.entrySet()) {
            Thing thing = entry.getKey();
            int requiredQuantity = entry.getValue();
            if (!contains(thing.getThingId()) || getQuantityOf(thing) < requiredQuantity) return false;

        }
        return true;
    }

    public int getQuantityOf(Thing thing) {
        if (thing == null) throw new EntityNotFoundException();
        for (StockLevel stockLevel : stockLevels) {
            if (stockLevel.contains(thing)) {
                return stockLevel.getQuantityInStock();
            }
        }
        return 0;
    }

    public int getQuantityOf(ThingId thingId) {
        if (thingId == null) throw new EntityIdNullException();
        for (StockLevel stockLevel : stockLevels) {
            if (stockLevel.contains(thingId)) {
                return stockLevel.getQuantityInStock();
            }
        }
        return 0;
    }

    public UUID getUUID() {
        return storageId.getId();
    }
}
