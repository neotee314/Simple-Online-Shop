package com.neotee.ecommercesystem.shopsystem.order.domain;

import com.neotee.ecommercesystem.core.AggregateRoot;
import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.OrderId;
import com.neotee.ecommercesystem.domainprimitives.OrderStatus;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.client.domain.Client;
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderPart> orderParts;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    private LocalDate submissionDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

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
        return new Order(OrderId.newId(), client);
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

    public Email getClientEmail() {
        return client != null ? client.getEmail() : null;
    }

    public void addOrderParts(Map<Product, Integer> partsWithQuantity) {
        if (partsWithQuantity == null || partsWithQuantity.isEmpty()) {
            throw new DomainValidationException("partsWithQuantity", "Parts mit Quantity darf nicht leer sein.");
        }
        if (status != OrderStatus.PENDING) {
            throw new DomainValidationException("status", "Kann nur zu ausstehenden Bestellungen Artikel hinzufügen.");
        }

        for (Map.Entry<Product, Integer> entry : partsWithQuantity.entrySet()) {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();

            if (product == null) {
                throw new DomainValidationException("product", "Product darf nicht null sein.");
            }
            if (quantity == null || quantity <= 0) {
                throw new DomainValidationException("quantity", "Quantity muss größer als 0 sein.");
            }

            OrderPart part = OrderPart.create(product, quantity);
            addOrderPart(part);
        }
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

    public void removeOrderPart(OrderPart part) {
        if (part == null) {
            throw new DomainValidationException("orderPart", "OrderPart darf nicht null sein.");
        }
        if (status != OrderStatus.PENDING) {
            throw new DomainValidationException("status", "Kann nur von ausstehenden Bestellungen Artikel entfernen.");
        }
        orderParts.remove(part);
    }

    public void updateOrderPartQuantity(Product product, int newQuantity) {
        if (product == null) {
            throw new DomainValidationException("product", "Product darf nicht null sein.");
        }
        if (newQuantity <= 0) {
            throw new DomainValidationException("quantity", "Quantity muss größer als 0 sein.");
        }
        if (status != OrderStatus.PENDING) {
            throw new DomainValidationException("status", "Kann nur bei ausstehenden Bestellungen die Menge ändern.");
        }

        for (OrderPart part : orderParts) {
            if (part.getProduct().getId().equals(product.getId())) {
                part.setOrderQuantity(newQuantity);
                return;
            }
        }
        throw new DomainValidationException("product", "Product nicht in der Bestellung gefunden.");
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

    public int getTotalQuantity() {
        return orderParts.stream()
                .mapToInt(OrderPart::getOrderQuantity)
                .sum();
    }

    public boolean isEmpty() {
        return orderParts.isEmpty();
    }

    public Map<Product, Integer> getOrderLineItemsMap() {
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