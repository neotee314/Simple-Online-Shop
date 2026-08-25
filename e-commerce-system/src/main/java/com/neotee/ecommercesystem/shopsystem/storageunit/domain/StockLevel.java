package com.neotee.ecommercesystem.shopsystem.storageunit.domain;

import com.neotee.ecommercesystem.core.AbstractEntity;
import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.domainprimitives.StockLevelId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StockLevel extends AbstractEntity<StockLevelId> {

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer quantityInStock;

    protected StockLevel(StockLevelId stockLevelId) {
        this.id = stockLevelId;
    }

    protected StockLevel(StockLevelId stockLevelId, Product product, Integer quantityInStock) {
        this.id = stockLevelId;
        this.product = product;
        this.quantityInStock = quantityInStock;
    }

    public static StockLevel create(Product product, Integer quantityInStock) {
        if (product == null)
            throw new DomainValidationException("StockLevel", "Product darf nicht null sein.");
        if (quantityInStock == null || quantityInStock < 0)
            throw new DomainValidationException("StockLevel", "Quantity muss größer oder gleich 0 sein.");

        return new StockLevel(StockLevelId.newId(), product, quantityInStock);
    }

    public void addToQuantity(Integer quantity) {
        if (quantity == null || quantity < 0)
            throw new DomainValidationException("StockLevel", "Quantity muss größer als 0 sein.");
        this.quantityInStock += quantity;
    }

    public void removeFromQuantity(Integer quantity) {
        if (quantity == null || quantity < 0)
            throw new DomainValidationException("StockLevel", "Quantity muss größer als 0 sein.");
        if (this.quantityInStock < quantity)
            throw new DomainValidationException("StockLevel", "Nicht genügend Lagerbestand vorhanden.");

        this.quantityInStock -= quantity;
    }

    public void changeStockTo(Integer newQuantity) {
        if (newQuantity == null || newQuantity < 0)
            throw new DomainValidationException("StockLevel", "Quantity muss größer oder gleich 0 sein.");
        this.quantityInStock = newQuantity;
    }

    public boolean contains(Product product) {
        if (product == null)
            throw new DomainValidationException("StockLevel", "Product darf nicht null sein.");
        return this.product.getId().equals(product.getId());
    }

    public boolean contains(ProductId productId) {
        if (productId == null)
            throw new DomainValidationException("StockLevel", "Product ID darf nicht null sein.");
        return this.product.getId().equals(productId);
    }

    public boolean isInStock() {
        return quantityInStock > 0;
    }

    public boolean isInStock(int requiredQuantity) {
        return quantityInStock >= requiredQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (StockLevel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}