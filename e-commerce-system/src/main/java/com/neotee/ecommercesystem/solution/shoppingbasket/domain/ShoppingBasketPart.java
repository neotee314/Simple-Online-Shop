package com.neotee.ecommercesystem.solution.shoppingbasket.domain;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.Money;
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
public class ShoppingBasketPart {

    @Id
    private UUID id;

    private UUID thingId;

    private int quantity;

    @Embedded
    private Money salesPrice;

    public ShoppingBasketPart(UUID thingId, int quantity) {
        if (thingId == null || quantity <= 0)
            throw new ShopException("Invalid thing ID or quantity must be greater than 0");
        this.id = thingId;
        this.thingId = thingId;
        this.quantity = quantity;
    }

    public ShoppingBasketPart(UUID thingId, int quantity, Money price) {
        if (thingId == null || quantity <= 0 || price == null)
            throw new ShopException("Invalid thing ID or quantity must be greater than 0");
        this.id = thingId;
        this.thingId = thingId;
        this.quantity = quantity;
        this.salesPrice = price;
    }

    // Increase the quantity of the part by the given amount
    public void increaseQuantity(int quantity) {
        this.quantity += quantity;
    }


    public void decreaseQuantity(int quantity) {
        if (this.quantity - quantity >= 0) {
            this.quantity -= quantity;
        }


    }

    public boolean contains(UUID thingId) {
        if (thingId == null) throw new ShopException("Thing ID must not be null");
        return this.thingId.equals(thingId);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingBasketPart that = (ShoppingBasketPart) o;
        return Objects.equals(getThingId(), that.getThingId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getThingId());
    }
}
