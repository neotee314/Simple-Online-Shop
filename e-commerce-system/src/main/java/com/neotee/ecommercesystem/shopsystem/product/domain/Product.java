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
        return new Product(ProductId.newId(), name, description, size, purchasePrice, salesPrice, 0);
    }

    public static Product create(String name, String description, Float size,
                                 Money purchasePrice, Money salesPrice, Integer stockQuantity) {
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
        return new Product(productId, name, description, size, purchasePrice, salesPrice, 0);
    }

    public static Product create(ProductId productId, String name, String description, Float size,
                                 Money purchasePrice, Money salesPrice, Integer stockQuantity) {
        if (productId == null) {
            throw new DomainValidationException("productId", "Product ID darf nicht null sein.");
        }
        if (stockQuantity == null || stockQuantity < 0) {
            throw new DomainValidationException("stockQuantity", "Stock Quantity muss größer oder gleich 0 sein.");
        }
        return new Product(productId, name, description, size, purchasePrice, salesPrice, stockQuantity);
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