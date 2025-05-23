package com.neotee.ecommercesystem.solution.order.domain;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.Email;
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
    private UUID orderId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrderPart> orderParts = new ArrayList<>();

    @Embedded
    private Email clientEmail;

    private LocalDate submissionDate;

    public Order(Email clientEmail) {
        if (clientEmail == null) throw new ShopException("Client email must not be null");
        orderId = UUID.randomUUID();
        submissionDate = LocalDate.now();
        this.clientEmail = clientEmail;
    }

    public void addOrderParts(Map<UUID, Integer> partsWithQuantity) {
        if (partsWithQuantity == null || partsWithQuantity.isEmpty()) {
            throw new ShopException("Order parts cannot be null or empty");
        }
        for (Map.Entry<UUID, Integer> entry : partsWithQuantity.entrySet()) {
            UUID partId = entry.getKey();
            Integer quantity = entry.getValue();

            if (quantity <= 0) {
                throw new ShopException("Quantity must be greater than zero for part: " + partId);
            }

            OrderPart part = new OrderPart(partId, quantity);
            addOrderPart(part);
        }
    }

    public void addOrderPart(OrderPart newPart) {
        if (newPart == null || newPart.getOrderQuantity() <= 0) {
            throw new ShopException("Order quantity must be greater than zero.");
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


    public Map<UUID, Integer> getOrderLineItemsMap() {
        Map<UUID, Integer> partsWithQuantity = new HashMap<>();
        for (OrderPart orderPart : orderParts) {
            partsWithQuantity.put(orderPart.getThingId(), orderPart.getOrderQuantity());
        }
        return partsWithQuantity;
    }


    public boolean contains(UUID thingId) {
        if (thingId == null) throw new ShopException("Thing ID must not be null");
        return orderParts.stream()
                .anyMatch(p -> p.contains(thingId));
    }
}
