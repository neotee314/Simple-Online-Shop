package com.neotee.ecommercesystem.shopsystem.storageunit.domain;

import com.neotee.ecommercesystem.shopsystem.core.AggregateRoot;
import com.neotee.ecommercesystem.domainprimitives.HomeAddress;
import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.domainprimitives.StorageUnitId;
import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.event.StockChangedEvent;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StorageUnit extends AggregateRoot<StorageUnitId> {

    private String name;

    @Embedded
    private HomeAddress address;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "storage_unit_id")
    private List<StockLevel> stockLevels;

    @Transient
    private List<Object> domainEvents = new ArrayList<>();

    protected StorageUnit(StorageUnitId storageId) {
        this.id = storageId;
        this.stockLevels = new ArrayList<>();
    }

    protected StorageUnit(StorageUnitId storageId, HomeAddress address, String name) {
        this.id = storageId;
        this.address = address;
        this.name = name;
        this.stockLevels = new ArrayList<>();
    }

    public static StorageUnit create(HomeAddress address, String name) {
        if (address == null) {
            throw new DomainValidationException("StorageUnit", "Address darf nicht null sein.");
        }
        if (name == null || name.isBlank()) {
            throw new DomainValidationException("StorageUnit", "Name darf nicht leer sein.");
        }
        return new StorageUnit(StorageUnitId.newId(), address, name);
    }

    public static StorageUnit create(StorageUnitId storageId, HomeAddress address, String name) {
        if (storageId == null) {
            throw new DomainValidationException("StorageUnit", "Storage ID darf nicht null sein.");
        }
        if (address == null) {
            throw new DomainValidationException("StorageUnit", "Address darf nicht null sein.");
        }
        if (name == null || name.isBlank()) {
            throw new DomainValidationException("StorageUnit", "Name darf nicht leer sein.");
        }
        return new StorageUnit(storageId, address, name);
    }

    public void addToStock(Product product, Integer quantity) {
        if (product == null) {
            throw new DomainValidationException("StorageUnit", "Product darf nicht null sein.");
        }
        if (quantity == null || quantity < 0) {
            throw new DomainValidationException("StorageUnit", "Quantity muss größer als 0 sein.");
        }

        var existingStock = findStockLevelByProduct(product);
        if (existingStock != null) {
            int oldQuantity = existingStock.getQuantityInStock();
            existingStock.addToQuantity(quantity);
            registerEvent(new StockChangedEvent(this.id, product.getId(), existingStock.getQuantityInStock(), oldQuantity));
            return;
        }

        var newStock = StockLevel.create(product, quantity);
        stockLevels.add(newStock);
        registerEvent(new StockChangedEvent(this.id, product.getId(), quantity, 0));
    }

    public void removeFromStock(ProductId productId, Integer quantity) {
        if (productId == null) {
            throw new DomainValidationException("StorageUnit", "Product ID darf nicht null sein.");
        }
        if (quantity == null || quantity < 0) {
            throw new DomainValidationException("StorageUnit", "Quantity muss größer als 0 sein.");
        }

        if (quantity == 0) {
            return;
        }

        var stockLevel = findStockLevelByProductId(productId);
        if (stockLevel == null)
            throw new DomainValidationException("StorageUnit", "Product nicht im Lager gefunden.");


        int oldQuantity = stockLevel.getQuantityInStock();
        stockLevel.removeFromQuantity(quantity);
        int newQuantity = stockLevel.getQuantityInStock();

        if (newQuantity <= 0) {
            stockLevels.remove(stockLevel);
        }

        registerEvent(new StockChangedEvent(this.id, productId, newQuantity, oldQuantity));
    }

    public void changeStockTo(Product product, Integer newTotalQuantity) {
        if (product == null) {
            throw new DomainValidationException("StorageUnit", "Product darf nicht null sein.");
        }
        if (newTotalQuantity == null || newTotalQuantity < 0) {
            throw new DomainValidationException("StorageUnit", "Quantity muss größer oder gleich 0 sein.");
        }

        var stockLevel = findStockLevelByProduct(product);
        if (stockLevel == null) {
            throw new DomainValidationException("StorageUnit", "Product nicht im Lager gefunden.");
        }

        int oldQuantity = stockLevel.getQuantityInStock();
        stockLevel.changeStockTo(newTotalQuantity);
        int newQuantity = stockLevel.getQuantityInStock();

        if (newQuantity <= 0) {
            stockLevels.remove(stockLevel);
        }

        registerEvent(new StockChangedEvent(this.id, product.getId(), newQuantity, oldQuantity));
    }

    public Integer getAvailableStock(ProductId productId) {
        if (productId == null) {
            throw new DomainValidationException("StorageUnit", "Product ID darf nicht null sein.");
        }
        var stockLevel = findStockLevelByProductId(productId);
        return stockLevel != null ? stockLevel.getQuantityInStock() : 0;
    }

    public boolean contains(ProductId productId) {
        if (productId == null) {
            throw new DomainValidationException("StorageUnit", "Product ID darf nicht null sein.");
        }
        return findStockLevelByProductId(productId) != null;
    }

    public boolean hasSufficientQuantityOf(Product product, int requiredQuantity) {
        if (product == null) {
            throw new DomainValidationException("StorageUnit", "Product darf nicht null sein.");
        }
        if (requiredQuantity < 0) {
            return false;
        }
        var stockLevel = findStockLevelByProduct(product);
        return stockLevel != null && stockLevel.getQuantityInStock() >= requiredQuantity;
    }

    public int getQuantityOf(Product product) {
        if (product == null) {
            throw new DomainValidationException("StorageUnit", "Product darf nicht null sein.");
        }
        var stockLevel = findStockLevelByProduct(product);
        return stockLevel != null ? stockLevel.getQuantityInStock() : 0;
    }

    public int getQuantityOf(ProductId productId) {
        if (productId == null) {
            throw new DomainValidationException("StorageUnit", "Product ID darf nicht null sein.");
        }
        var stockLevel = findStockLevelByProductId(productId);
        return stockLevel != null ? stockLevel.getQuantityInStock() : 0;
    }

    public Integer getDistanceToClient(ZipCode clientZipCode) {
        if (clientZipCode == null) {
            throw new DomainValidationException("StorageUnit", "Client ZipCode darf nicht null sein.");
        }
        return clientZipCode.difference(address.getZipCode());
    }

    public Map<Product, Integer> getServableItems(Map<Product, Integer> requiredItems) {
        if (requiredItems == null || requiredItems.isEmpty()) {
            return new LinkedHashMap<>();
        }

        var servableItems = new LinkedHashMap<Product, Integer>();

        for (var entry : requiredItems.entrySet()) {
            var product = entry.getKey();
            var required = entry.getValue();
            var available = getQuantityOf(product);

            if (available > 0) {
                var taken = Math.min(available, required);
                servableItems.put(product, taken);
            }
        }

        return servableItems;
    }

    public Integer getTotalContributingItems(Map<Product, Integer> requiredItems) {
        if (requiredItems == null || requiredItems.isEmpty()) {
            return 0;
        }
        return (int) requiredItems.entrySet().stream()
                .filter(entry -> hasSufficientQuantityOf(entry.getKey(), entry.getValue()))
                .count();
    }

    public List<Product> getAvailableProducts() {
        return stockLevels.stream()
                .filter(stock -> stock.getQuantityInStock() > 0)
                .map(StockLevel::getProduct)
                .toList();
    }

    public boolean isEmpty() {
        return stockLevels.isEmpty() || stockLevels.stream().allMatch(s -> s.getQuantityInStock() <= 0);
    }

    private StockLevel findStockLevelByProduct(Product product) {
        return stockLevels.stream()
                .filter(stock -> stock.contains(product))
                .findFirst()
                .orElse(null);
    }

    private StockLevel findStockLevelByProductId(ProductId productId) {
        return stockLevels.stream()
                .filter(stock -> stock.contains(productId))
                .findFirst()
                .orElse(null);
    }

    private void registerEvent(Object event) {
        if (domainEvents == null) {
            domainEvents = new ArrayList<>();
        }
        domainEvents.add(event);
    }

    public List<Object> getDomainEvents() {
        return new ArrayList<>(domainEvents);
    }

    public boolean canDelete() {
        return stockLevels.isEmpty() || stockLevels.stream().allMatch(s -> s.getQuantityInStock() == 0);
    }

    public void clearEvents() {
        domainEvents.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (StorageUnit) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}