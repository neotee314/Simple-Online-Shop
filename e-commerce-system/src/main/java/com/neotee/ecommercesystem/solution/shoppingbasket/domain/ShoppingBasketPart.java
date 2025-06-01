package com.neotee.ecommercesystem.solution.shoppingbasket.domain;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.solution.thing.domain.Thing;
import com.neotee.ecommercesystem.solution.thing.domain.ThingId;
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
    private ShoppingBasketPartId id;

    @ManyToOne
    private Thing thing;

    private Integer quantity;

    @Embedded
    private Money salesPrice;



    public ShoppingBasketPart(Thing thing, int quantity, Money price) {
        if (thing == null || quantity <= 0 || price == null)
            throw new ShopException("Invalid thing ID or quantity must be greater than 0");
        this.id = new ShoppingBasketPartId();
        this.thing = thing;
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
    public UUID getThingId() {
        return thing.getThingId().getId() ;
    }
    public boolean contains(ThingId thingId) {
        if (thingId == null) throw new ShopException("Thing ID must not be null");
        return this.thing.getThingId().equals(thingId);
    }

    public boolean contains(UUID thingId) {
        if (thingId == null) throw new ShopException("Thing ID must not be null");
        return this.thing.getThingId().getId().equals(thingId);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingBasketPart that = (ShoppingBasketPart) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

}
