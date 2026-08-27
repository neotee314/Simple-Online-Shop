package com.neotee.ecommercesystem.shopsystem.order.domain;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.OrderId;
import com.neotee.ecommercesystem.domainprimitives.OrderStatus;
import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.client.domain.Client;
import com.neotee.ecommercesystem.shopsystem.core.AggregateRoot;
import com.neotee.ecommercesystem.events.OrderCreatedEvent;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order extends AggregateRoot<OrderId> {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<OrderPart> orderParts;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    private LocalDate submissionDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Transient
    private List<Object> domainEvents = new ArrayList<>();

    protected Order(OrderId orderId) {
        this.id = orderId;
        this.orderParts = new ArrayList<>();
        this.submissionDate = LocalDate.now();
        this.status = OrderStatus.PENDING;
    }

    protected Order(OrderId orderId, Client client) {
        this.id = orderId;
        this.orderParts = new ArrayList<>();
        this.client = client;
        this.submissionDate = LocalDate.now();
        this.status = OrderStatus.PENDING;
    }

    public static Order create(Client client) {
        if (client == null) {
            throw new DomainValidationException("client", "Client darf nicht null sein.");
        }
        var order = new Order(OrderId.newId(), client);
        order.registerEvent(new OrderCreatedEvent(order.getId()));
        return order;
    }

    public static Order create(OrderId orderId, Client client) {
        if (orderId == null) {
            throw new DomainValidationException("orderId", "Order ID darf nicht null sein.");
        }
        if (client == null) {
            throw new DomainValidationException("client", "Client darf nicht null sein.");
        }
        return new Order(orderId, client);
    }

    private void registerEvent(Object event) {
        if (domainEvents == null) {
            domainEvents = new ArrayList<>();
        }
        domainEvents.add(event);
    }

    public List<Object> getDomainEvents() {
        return new ArrayList<>(domainEvents);
    }

    public void clearEvents() {
        domainEvents.clear();
    }

    public Email getClientEmail() {
        return client != null ? client.getEmail() : null;
    }


    public void addOrderPart(OrderPart newPart) {
        if (newPart == null) {
            throw new DomainValidationException("orderPart", "OrderPart darf nicht null sein.");
        }
        if (newPart.getOrderQuantity() <= 0) {
            throw new DomainValidationException("quantity", "Quantity muss größer als 0 sein.");
        }
        if (status != OrderStatus.PENDING) {
            throw new DomainValidationException("status", "Kann nur zu ausstehenden Bestellungen Artikel hinzufügen.");
        }

        for (OrderPart existingPart : orderParts) {
            if (existingPart.getProduct().getId().equals(newPart.getProduct().getId())) {
                existingPart.increaseQuantity(newPart.getOrderQuantity());
                return;
            }
        }

        this.orderParts.add(newPart);
    }


    public void submit() {
        if (orderParts.isEmpty()) {
            throw new DomainValidationException("orderParts", "Bestellung ohne Artikel kann nicht abgeschickt werden.");
        }
        if (status != OrderStatus.PENDING) {
            throw new DomainValidationException("status", "Nur ausstehende Bestellungen können abgeschickt werden.");
        }
        this.status = OrderStatus.SUBMITTED;
        this.submissionDate = LocalDate.now();
    }

    public void cancel() {
        if (status == OrderStatus.DELIVERED) {
            throw new DomainValidationException("status", "Bereits gelieferte Bestellungen können nicht storniert werden.");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public void deliver() {
        if (status != OrderStatus.SUBMITTED) {
            throw new DomainValidationException("status", "Nur abgeschickte Bestellungen können geliefert werden.");
        }
        this.status = OrderStatus.DELIVERED;
    }


    public boolean isEmpty() {
        return orderParts.isEmpty();
    }

    public Map<Product, Integer> getOrderLine() {
        Map<Product, Integer> partsWithQuantity = new HashMap<>();
        for (OrderPart orderPart : orderParts) {
            partsWithQuantity.put(orderPart.getProduct(), orderPart.getOrderQuantity());
        }
        return partsWithQuantity;
    }

    public boolean containsProduct(Product product) {
        if (product == null)
            throw new DomainValidationException("productId", "Product ID darf nicht null sein.");

        return orderParts.stream()
                .anyMatch(p -> p.getProduct().equals(product));
    }

    public ZipCode getClientZipCode() {
        return client.getHomeAddress().getZipCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}