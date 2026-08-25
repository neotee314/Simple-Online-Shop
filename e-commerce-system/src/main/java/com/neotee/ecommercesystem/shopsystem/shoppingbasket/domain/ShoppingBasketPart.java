package com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain;

import com.neotee.ecommercesystem.core.AbstractEntity;
import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.domainprimitives.ShoppingBasketPartId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ShoppingBasketPart extends AbstractEntity<ShoppingBasketPartId> {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer quantity;

    @Embedded
    private Money salesPrice;

    protected ShoppingBasketPart(ShoppingBasketPartId partId) {
        this.id = partId;
    }

    public static ShoppingBasketPart create(Product product, int quantity, Money price) {
        if (product == null)
            throw new DomainValidationException("ShoppingBasketPart", "Product darf nicht null sein.");
        if (quantity <= 0)
            throw new DomainValidationException("ShoppingBasketPart", "Quantity muss größer als 0 sein.");
        if (price == null)
            throw new DomainValidationException("ShoppingBasketPart", "Preis darf nicht null sein.");

        var part = new ShoppingBasketPart(ShoppingBasketPartId.newId());
        part.product = product;
        part.quantity = quantity;
        part.salesPrice = price;
        return part;
    }

    public void increaseQuantity(int quantity) {
        if (quantity <= 0)
            throw new DomainValidationException("ShoppingBasketPart", "Quantity muss größer als 0 sein.");
        this.quantity += quantity;
    }

    public void decreaseQuantity(int quantity) {
        if (quantity <= 0)
            throw new DomainValidationException("ShoppingBasketPart", "Quantity muss größer als 0 sein.");
        if (this.quantity - quantity < 0)
            throw new DomainValidationException("ShoppingBasketPart", "Kann nicht mehr entfernen als vorhanden ist.");
        this.quantity -= quantity;
    }

    public UUID getProductId() {
        return product.getId().getId();
    }

    public boolean contains(ProductId productId) {
        if (productId == null)
            throw new DomainValidationException("ShoppingBasketPart", "Product ID darf nicht null sein.");
        return this.product.getId().equals(productId);
    }

    public boolean contains(UUID productId) {
        if (productId == null)
            throw new DomainValidationException("ShoppingBasketPart", "Product ID darf nicht null sein.");
        return this.product.getId().getId().equals(productId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (ShoppingBasketPart) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}