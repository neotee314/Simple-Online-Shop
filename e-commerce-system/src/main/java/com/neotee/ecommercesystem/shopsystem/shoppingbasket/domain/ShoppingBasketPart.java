package com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain;

import com.neotee.ecommercesystem.shopsystem.core.AbstractEntity;
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

@Entity
@Table(name = "shopping_basket_part")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ShoppingBasketPart extends AbstractEntity<ShoppingBasketPartId> {

    @ManyToOne
    private Product product;

    private int quantity;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "sales_price_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "sales_price_currency"))
    })
    private Money salesPrice;

    protected ShoppingBasketPart(ShoppingBasketPartId partId) {
        this.id = partId;
    }

    protected ShoppingBasketPart(ShoppingBasketPartId partId, Product product, int quantity, Money salesPrice) {
        this.id = partId;
        this.product = product;
        this.quantity = quantity;
        this.salesPrice = salesPrice;
    }
    public Money getTotalPrice(){
        return (Money) salesPrice.multiplyBy(quantity);
    }

    public static ShoppingBasketPart create(Product product, int quantity, Money salesPrice) {
        if (product == null) {
            throw new DomainValidationException("ShoppingBasketPart", "Product darf nicht null sein.");
        }
        if (quantity <= 0) {
            throw new DomainValidationException("ShoppingBasketPart", "Quantity muss größer als 0 sein.");
        }
        if (salesPrice == null) {
            throw new DomainValidationException("ShoppingBasketPart", "Sales Price darf nicht null sein.");
        }
        return new ShoppingBasketPart(ShoppingBasketPartId.newId(), product, quantity, salesPrice);
    }

    public void increaseQuantity(int quantity) {
        if (quantity <= 0) {
            throw new DomainValidationException("quantity", "Quantity muss größer als 0 sein.");
        }
        this.quantity += quantity;
    }

    public void decreaseQuantity(int quantity) {
        if (quantity <= 0) {
            throw new DomainValidationException("quantity", "Quantity muss größer als 0 sein.");
        }
        if (quantity > this.quantity) {
            throw new DomainValidationException("quantity", "Kann nicht mehr entfernen als vorhanden ist.");
        }
        this.quantity -= quantity;
    }

    public ProductId getProductId() {
        return product.getId();
    }

    public boolean contains(ProductId productId) {
        return product.getId().equals(productId);
    }

    public void changeQuantity(Integer availableStock) {
        this.quantity = availableStock;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingBasketPart that = (ShoppingBasketPart) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}