package com.neotee.ecommercesystem.shopsystem.product.domain;

import com.neotee.ecommercesystem.core.AggregateRoot;
import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Product extends AggregateRoot<ProductId> {

    @Setter
    private String name;

    @Setter
    private String description;

    @Setter
    private Float size;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "purchase_price")),
            @AttributeOverride(name = "currency", column = @Column(name = "purchase_currency"))
    })
    private Money purchasePrice;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "sales_price")),
            @AttributeOverride(name = "currency", column = @Column(name = "sales_currency"))
    })
    private Money salesPrice;

    @Setter
    private Integer stockQuantity;

    protected Product(ProductId productId) {
        this.id = productId;
        this.stockQuantity = 0;
    }

    protected Product(ProductId productId, String name, String description, Float size,
                      Money purchasePrice, Money salesPrice, Integer stockQuantity) {
        this.id = productId;
        this.name = name;
        this.description = description;
        this.size = size;
        this.purchasePrice = purchasePrice;
        this.salesPrice = salesPrice;
        this.stockQuantity = stockQuantity != null ? stockQuantity : 0;
    }

    public static Product create(String name, String description, Float size,
                                 Money purchasePrice, Money salesPrice) {
        validateProductData(name, description, size, purchasePrice, salesPrice);
        return new Product(ProductId.newId(), name, description, size, purchasePrice, salesPrice, 0);
    }

    public static Product create(String name, String description, Float size,
                                 Money purchasePrice, Money salesPrice, Integer stockQuantity) {
        validateProductData(name, description, size, purchasePrice, salesPrice);
        if (stockQuantity == null || stockQuantity < 0) {
            throw new DomainValidationException("stockQuantity", "Stock Quantity muss größer oder gleich 0 sein.");
        }
        return new Product(ProductId.newId(), name, description, size, purchasePrice, salesPrice, stockQuantity);
    }

    public static Product create(ProductId productId, String name, String description, Float size,
                                 Money purchasePrice, Money salesPrice) {
        if (productId == null) {
            throw new DomainValidationException("productId", "Product ID darf nicht null sein.");
        }
        validateProductData(name, description, size, purchasePrice, salesPrice);
        return new Product(productId, name, description, size, purchasePrice, salesPrice, 0);
    }

    public static Product create(ProductId productId, String name, String description, Float size,
                                 Money purchasePrice, Money salesPrice, Integer stockQuantity) {
        if (productId == null) {
            throw new DomainValidationException("productId", "Product ID darf nicht null sein.");
        }
        validateProductData(name, description, size, purchasePrice, salesPrice);
        if (stockQuantity == null || stockQuantity < 0) {
            throw new DomainValidationException("stockQuantity", "Stock Quantity muss größer oder gleich 0 sein.");
        }
        return new Product(productId, name, description, size, purchasePrice, salesPrice, stockQuantity);
    }

    private static void validateProductData(String name, String description, Float size,
                                            Money purchasePrice, Money salesPrice) {
        if (name == null || name.isBlank()) {
            throw new DomainValidationException("name", "Name darf nicht leer sein.");
        }
        if (description == null || description.isBlank()) {
            throw new DomainValidationException("description", "Beschreibung darf nicht leer sein.");
        }
        if (size != null && size <= 0) {
            throw new DomainValidationException("size", "Größe muss größer als 0 sein.");
        }
        if (purchasePrice == null) {
            throw new DomainValidationException("purchasePrice", "Einkaufspreis darf nicht null sein.");
        }
        if (salesPrice == null) {
            throw new DomainValidationException("salesPrice", "Verkaufspreis darf nicht null sein.");
        }
        if (purchasePrice.getAmount() <= 0) {
            throw new DomainValidationException("purchasePrice", "Einkaufspreis muss größer als 0 sein.");
        }
        if (salesPrice.getAmount() <= 0) {
            throw new DomainValidationException("salesPrice", "Verkaufspreis muss größer als 0 sein.");
        }
        if (purchasePrice.largerThan(salesPrice)) {
            throw new DomainValidationException("purchasePrice", "Einkaufspreis darf nicht größer als Verkaufspreis sein.");
        }
    }

    public void updatePrice(Money newPurchasePrice, Money newSalesPrice) {
        if (newPurchasePrice == null) {
            throw new DomainValidationException("purchasePrice", "Einkaufspreis darf nicht null sein.");
        }
        if (newSalesPrice == null) {
            throw new DomainValidationException("salesPrice", "Verkaufspreis darf nicht null sein.");
        }
        if (newPurchasePrice.getAmount() <= 0) {
            throw new DomainValidationException("purchasePrice", "Einkaufspreis muss größer als 0 sein.");
        }
        if (newSalesPrice.getAmount() <= 0) {
            throw new DomainValidationException("salesPrice", "Verkaufspreis muss größer als 0 sein.");
        }
        if (newPurchasePrice.largerThan(newSalesPrice)) {
            throw new DomainValidationException("purchasePrice", "Einkaufspreis darf nicht größer als Verkaufspreis sein.");
        }
        this.purchasePrice = newPurchasePrice;
        this.salesPrice = newSalesPrice;
    }

    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new DomainValidationException("quantity", "Quantity muss größer als 0 sein.");
        }
        this.stockQuantity += quantity;
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new DomainValidationException("quantity", "Quantity muss größer als 0 sein.");
        }
        if (this.stockQuantity < quantity) {
            throw new DomainValidationException("stockQuantity", "Nicht genügend Lagerbestand vorhanden.");
        }
        this.stockQuantity -= quantity;
    }

    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }

    public boolean isInStock(int quantity) {
        return stockQuantity != null && stockQuantity >= quantity;
    }

    public Money getSellingPrice() {
        return (Money) Money.of(salesPrice.getAmount(), salesPrice.getCurrency());
    }

    public Money getPurchasePrice() {
        return (Money) Money.of(purchasePrice.getAmount(), purchasePrice.getCurrency());
    }

    public boolean hasValidPrices() {
        return purchasePrice != null && salesPrice != null &&
                purchasePrice.getAmount() > 0 && salesPrice.getAmount() > 0 &&
                !purchasePrice.largerThan(salesPrice);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}