package com.neotee.ecommercesystem.shopsystem.order.domain;

import com.neotee.ecommercesystem.exception.EntityIdNullException;
import com.neotee.ecommercesystem.exception.QuantityNegativeException;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.product.domain.ProductId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OrderPart {

    @Id
    private OrderPartId id;

    @ManyToOne
    private Product product;

    private Integer orderQuantity;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderPart orderPart = (OrderPart) o;
        return Objects.equals(getId(), orderPart.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }




    public OrderPart(Product product, int quantity) {
        if (product == null) throw new EntityIdNullException();
        if (quantity <= 0) throw new QuantityNegativeException();
        this.id = new OrderPartId();
        this.product = product;
        this.orderQuantity = quantity;
    }

    public void increaseQuantity(int amount) {
        if (amount <= 0) {
            throw new QuantityNegativeException();
        }
        this.orderQuantity += amount;
    }

    public boolean contains(UUID thingId) {
        if (thingId == null) throw new EntityIdNullException();
        return this.product.getProductId().getId().equals(thingId);
    }


    public ProductId getThingId() {
        return product.getProductId();
    }
}
