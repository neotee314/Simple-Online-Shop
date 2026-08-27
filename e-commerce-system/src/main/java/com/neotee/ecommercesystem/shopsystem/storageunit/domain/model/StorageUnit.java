package com.neotee.ecommercesystem.shopsystem.storageunit.domain.model;

import com.neotee.ecommercesystem.domainprimitives.HomeAddress;
import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.domainprimitives.StorageUnitId;
import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.exceptions.EntityNotFoundException;
import com.neotee.ecommercesystem.shopsystem.core.AggregateRoot;
import com.neotee.ecommercesystem.events.StockChangedEvent;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        validateAddress(address);
        validateName(name);
        return new StorageUnit(StorageUnitId.newId(), address, name);
    }

    public static StorageUnit create(StorageUnitId storageId, HomeAddress address, String name) {
        if (storageId == null) throw new DomainValidationException("StorageUnit", "Storage ID must not be null.");
        validateAddress(address);
        validateName(name);
        return new StorageUnit(storageId, address, name);
    }

    public Integer getContributingItemCount(Map<Product, Integer> requiredItems) {
        if (requiredItems == null || requiredItems.isEmpty()) return 0;
        return (int) requiredItems.entrySet().stream()
                .filter(entry -> hasSufficientQuantity(entry.getKey(), entry.getValue()))
                .count();
    }

    public Integer getDistanceToClient(ZipCode clientZipCode) {
        if (clientZipCode == null)
            throw new DomainValidationException("StorageUnit", "Client ZIP code must not be null.");
        return clientZipCode.difference(address.getZipCode());
    }

    public Integer getAvailableStock(Product product) {
        var stockLevel = findStockLevelByProduct(product);
        return stockLevel == null ? 0 : stockLevel.getQuantityInStock();
    }

    public boolean hasSufficientQuantity(Product product, Integer requiredQuantity) {
        if (product == null) throw new DomainValidationException("StorageUnit", "Product must not be null.");
        if (requiredQuantity < 0) return false;
        var stockLevel = findStockLevelByProduct(product);
        return stockLevel != null && stockLevel.getQuantityInStock() >= requiredQuantity;
    }

    public Integer getStockOf(Product product) {
        if (product == null) throw new DomainValidationException("StorageUnit", "Product must not be null.");
        var stockLevel = findStockLevelByProduct(product);
        return stockLevel == null ? 0 : stockLevel.getQuantityInStock();
    }


    public boolean isEmpty() {
        return stockLevels.isEmpty() || stockLevels.stream().allMatch(stock -> stock.getQuantityInStock() <= 0);
    }

    public void addToStock(Product product, Integer quantity) {
        if (product == null) throw new DomainValidationException("StorageUnit", "Product must not be null.");
        validateQuantity(quantity);

        var existingStock = findStockLevelByProduct(product);

        if (existingStock != null) {
            var oldQuantity = existingStock.getQuantityInStock();
            existingStock.addToQuantity(quantity);
            registerEvent(new StockChangedEvent(id, product.getId(), existingStock.getQuantityInStock(), oldQuantity));
            return;
        }

        stockLevels.add(StockLevel.create(product, quantity));
        registerEvent(new StockChangedEvent(id, product.getId(), quantity, 0));
    }

    public void removeFromStock(Product product, Integer quantity) {
        if (product == null) throw new DomainValidationException("StorageUnit", "Product must not be null.");
        if (quantity == 0) return;

        var stockLevel = findStockLevelByProductId(product.getId());
        if (stockLevel == null)
            throw new EntityNotFoundException("StorageUnit", "Product not found in storage unit.");

        var oldQuantity = stockLevel.getQuantityInStock();
        stockLevel.removeFromQuantity(quantity);
        var newQuantity = stockLevel.getQuantityInStock();

        if (newQuantity <= 0) stockLevels.remove(stockLevel);

        registerEvent(new StockChangedEvent(id, product.getId(), newQuantity, oldQuantity));
    }


    public void changeStockTo(Product product, Integer newTotalQuantity) {
        if (product == null) throw new DomainValidationException("StorageUnit", "Product must not be null.");
        validateQuantity(newTotalQuantity);

        var stockLevel = findStockLevelByProduct(product);
        if (stockLevel == null)
            throw new DomainValidationException("StorageUnit", "Product not found in storage unit.");

        var oldQuantity = stockLevel.getQuantityInStock();
        stockLevel.changeStockTo(newTotalQuantity);
        var newQuantity = stockLevel.getQuantityInStock();

        if (newQuantity <= 0) stockLevels.remove(stockLevel);

        registerEvent(new StockChangedEvent(id, product.getId(), newQuantity, oldQuantity));
    }

    public StockLevel findStockLevelByProduct(Product product) {
        if (product == null) throw new DomainValidationException("StorageUnit", "Product must not be null.");
        return stockLevels.stream()
                .filter(stock -> stock.contains(product))
                .findFirst()
                .orElse(null);
    }

    public List<Object> getDomainEvents() {
        return new ArrayList<>(domainEvents);
    }

    public void clearEvents() {
        domainEvents.clear();
    }


    private StockLevel findStockLevelByProductId(ProductId productId) {
        return stockLevels.stream()
                .filter(stock -> stock.contains(productId))
                .findFirst()
                .orElse(null);
    }

    private void registerEvent(Object event) {
        if (domainEvents == null) domainEvents = new ArrayList<>();
        domainEvents.add(event);
    }

    private static void validateAddress(HomeAddress address) {
        if (address == null) throw new DomainValidationException("StorageUnit", "Address must not be null.");
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank())
            throw new DomainValidationException("StorageUnit", "Name must not be empty.");
    }

    private static void validateProductId(ProductId productId) {
        if (productId == null) throw new DomainValidationException("StorageUnit", "Product ID must not be null.");
    }

    private static void validateQuantity(Integer quantity) {
        if (quantity == null || quantity < 0)
            throw new DomainValidationException("StorageUnit", "Quantity must be greater than or equal to 0.");
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