package com.neotee.ecommercesystem.shopsystem.storageunit.domain;

import com.neotee.ecommercesystem.exception.ShopException;
import com.neotee.ecommercesystem.domainprimitives.HomeAddress;
import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.exception.*;
import com.neotee.ecommercesystem.exception.EntityNotFoundException;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.product.domain.ProductId;
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

    public void addToStock(Product product, Integer quantity) {
        if (product == null) throw new EntityNotFoundException();
        if (quantity == null) throw new ValueObjectNullOrEmptyException();
        if (quantity < 0) throw new QuantityNegativeException();

        for (StockLevel stockLevel : stockLevels) {
            if (stockLevel.contains(product))
                stockLevel.addToQuantity(quantity);
        }
        StockLevel stockLevel = new StockLevel(product, quantity);
        stockLevels.add(stockLevel);
    }


    public void removeFromStock(ProductId productId, Integer removeQuantity) {
        if (productId == null) throw new EntityIdNullException();

        for (StockLevel stockLevel : stockLevels) {
            if (stockLevel.contains(productId)) {
                stockLevel.removeFromQuantity(removeQuantity);
                if (stockLevel.getQuantityInStock() <= 0) {
                    stockLevels.remove(stockLevel);
                }
                return;
            }
        }
    }


    public void changeStockTo(Product product, Integer newTotalQuantity) {
        if (product == null) throw new EntityNotFoundException();
        for (StockLevel stockLevel : stockLevels) {
            if (stockLevel.contains(product)) {
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
            if (stockLevel.contains(new ProductId(thingId))) {
                return stockLevel.getQuantityInStock();
            }
        }
        return 0;
    }

    public Integer getTotalContributingItems(Map<Product, Integer> items) {
        Integer totalContributingItems = 0;

        for (Product product : items.keySet()) {
            if (this.hasSufficientQuantityOf(product, items.get(product)))
                totalContributingItems += 1;


        }
        return totalContributingItems;
    }


    public boolean contains(ProductId productId) {
        if (productId == null) throw new EntityIdNullException();
        for (StockLevel stockLevel : stockLevels) {
            if (stockLevel.contains(productId)) {
                return true;
            }
        }
        return false;
    }

    public Integer getDistanceToClient(ZipCode clientZipCode) {
        if (clientZipCode == null) throw new ValueObjectNullOrEmptyException();
        return clientZipCode.difference(address.getZipCode());
    }


    public boolean hasSufficientQuantityOf(Product product, int requiredQuantity) {
        if (requiredQuantity < 0) return false;
        if (product == null) throw new EntityNotFoundException();
        if (!contains(product.getProductId())) return false;
        return getQuantityOf(product) >= requiredQuantity;
    }

    public Map<Product, Integer> getServableItems(Map<Product, Integer> remainingItems) {
        if (remainingItems == null) throw new ShopException("Remaining items must not be null");
        Map<Product, Integer> canServeItems = new LinkedHashMap<>();

        // Sort entries by descending quantity
        List<Map.Entry<Product, Integer>> sortedEntries = new ArrayList<>(remainingItems.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        for (Map.Entry<Product, Integer> entry : sortedEntries) {
            Product product = entry.getKey();
            int requiredQuantity = entry.getValue();

            if (this.hasSufficientQuantityOf(product, requiredQuantity)) {
                canServeItems.put(product, requiredQuantity);
                if (requiredQuantity >= 10) break;
            }
        }

        return canServeItems;
    }



    public int getQuantityOf(Product product) {
        if (product == null) throw new EntityNotFoundException();
        for (StockLevel stockLevel : stockLevels) {
            if (stockLevel.contains(product)) {
                return stockLevel.getQuantityInStock();
            }
        }
        return 0;
    }

    public int getQuantityOf(ProductId productId) {
        if (productId == null) throw new EntityIdNullException();
        for (StockLevel stockLevel : stockLevels) {
            if (stockLevel.contains(productId)) {
                return stockLevel.getQuantityInStock();
            }
        }
        return 0;
    }

    public UUID getUUID() {
        return storageId.getId();
    }
}
