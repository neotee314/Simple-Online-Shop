package com.neotee.ecommercesystem.solution.order.domain;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.exception.EntityIdNullException;
import com.neotee.ecommercesystem.exception.QuantityNegativeException;
import com.neotee.ecommercesystem.exception.ValueObjectNullOrEmptyException;
import com.neotee.ecommercesystem.solution.thing.domain.Thing;
import com.neotee.ecommercesystem.solution.thing.domain.ThingId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Getter
@Setter
@Table(name = "MY_FANTASTIC_ORDER_TABLE")
@NoArgsConstructor
public class Order {

    @Id
    private OrderId orderId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrderPart> orderParts = new ArrayList<>();

    @Embedded
    private Email clientEmail;

    private LocalDate submissionDate;

    public Order(Email clientEmail) {
        if (clientEmail == null) throw new ValueObjectNullOrEmptyException();
        orderId = new OrderId();
        submissionDate = LocalDate.now();
        this.clientEmail = clientEmail;
    }

    public void addOrderParts(Map<Thing, Integer> partsWithQuantity) {
        if (partsWithQuantity == null || partsWithQuantity.isEmpty()) {
            throw new EntityNotFoundException();
        }
        for (Map.Entry<Thing, Integer> entry : partsWithQuantity.entrySet()) {
            Thing thing = entry.getKey();
            Integer quantity = entry.getValue();

            if (quantity <= 0) {
                throw new QuantityNegativeException();
            }

            OrderPart part = new OrderPart(thing, quantity);
            addOrderPart(part);
        }
    }

    public void addOrderPart(OrderPart newPart) {
        if (newPart == null || newPart.getOrderQuantity() <= 0) {
            throw new QuantityNegativeException();
        }

        for (OrderPart existingPart : orderParts) {
            if (existingPart.equals(newPart)) {
                existingPart.increaseQuantity(newPart.getOrderQuantity());
                return;
            }
        }

        this.orderParts.add(newPart);
    }


    public int getTotalQuantity() {
        return orderParts.stream()
                .mapToInt(OrderPart::getOrderQuantity)
                .sum();
    }

    public boolean isEmpty() {
        return orderParts.isEmpty();
    }


    public Map<Thing, Integer> getOrderLineItemsMap() {
        Map<Thing, Integer> partsWithQuantity = new HashMap<>();
        for (OrderPart orderPart : orderParts) {
            partsWithQuantity.put(orderPart.getThing(), orderPart.getOrderQuantity());
        }
        return partsWithQuantity;
    }


    public boolean contains(UUID thingId) {
        if (thingId == null) throw new EntityIdNullException();
        return orderParts.stream()
                .anyMatch(p -> p.contains(thingId));
    }
}
