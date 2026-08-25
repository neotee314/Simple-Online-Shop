package com.neotee.ecommercesystem.shopsystem.order.domain;

import com.neotee.ecommercesystem.core.AbstractEntity;
import com.neotee.ecommercesystem.domainprimitives.OrderPartId;
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
public class OrderPart extends AbstractEntity<OrderPartId> {

    @ManyToOne
    private Product product;

    private Integer orderQuantity;

    protected OrderPart(OrderPartId orderPartId) {
        this.id = orderPartId;
    }

    protected OrderPart(OrderPartId orderPartId, Product product, int quantity) {
        this.id = orderPartId;
        this.product = product;
        this.orderQuantity = quantity;
    }

    public static OrderPart create(Product product, int quantity) {
        if (product == null) {
            throw new DomainValidationException("product", "Product darf nicht null sein.");
        }
        if (quantity <= 0) {
            throw new DomainValidationException("quantity", "Quantity muss größer als 0 sein.");
        }
        return new OrderPart(OrderPartId.newId(), product, quantity);
    }

    public static OrderPart create(OrderPartId orderPartId, Product product, int quantity) {
        if (orderPartId == null) {
            throw new DomainValidationException("orderPartId", "OrderPart ID darf nicht null sein.");
        }
        if (product == null) {
            throw new DomainValidationException("product", "Product darf nicht null sein.");
        }
        if (quantity <= 0) {
            throw new DomainValidationException("quantity", "Quantity muss größer als 0 sein.");
        }
        return new OrderPart(orderPartId, product, quantity);
    }

    public void increaseQuantity(int amount) {
        if (amount <= 0) {
            throw new DomainValidationException("amount", "Amount muss größer als 0 sein.");
        }
        this.orderQuantity += amount;
    }

    public void setOrderQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new DomainValidationException("quantity", "Quantity muss größer als 0 sein.");
        }
        this.orderQuantity = newQuantity;
    }

    public boolean containsProduct(UUID productId) {
        if (productId == null) {
            throw new DomainValidationException("productId", "Product ID darf nicht null sein.");
        }
        return this.product.getId().getId().equals(productId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderPart orderPart = (OrderPart) o;
        return Objects.equals(id, orderPart.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}