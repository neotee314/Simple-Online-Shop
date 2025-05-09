package com.neotee.ecommercesystem.solution.order.domain;

import com.neotee.ecommercesystem.ShopException;
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
    private UUID id;

    private UUID thingId;

    private Integer orderQuantity;


    public OrderPart(UUID thingId, int quantity) {
        if (thingId == null || quantity <= 0) throw new ShopException("Invalid thing ID or quantity must be greater than 0");
        this.id = UUID.randomUUID();
        this.thingId = thingId;
        this.orderQuantity = quantity;
    }

    public void increaseQuantity(int amount) {
        if (amount <= 0) {
            throw new ShopException("Increase amount must be greater than zero.");
        }
        this.orderQuantity += amount;
    }

    public void decreaseQuantity(int amount) {
        if (amount <= 0) {
            throw new ShopException("Decrease amount must be greater than zero.");
        }
        if (amount > this.orderQuantity) {
            throw new ShopException("Cannot decrease more than existing quantity.");
        }
        this.orderQuantity -= amount;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderPart orderPart = (OrderPart) o;
        return Objects.equals(getThingId(), orderPart.getThingId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getThingId());
    }

    public boolean contains(UUID thingId) {
        if (thingId == null) throw new ShopException("Thing ID must not be null");
        return this.thingId.equals(thingId);
    }
}
