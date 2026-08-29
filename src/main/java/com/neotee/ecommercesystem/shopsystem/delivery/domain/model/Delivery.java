package com.neotee.ecommercesystem.shopsystem.delivery.domain.model;

import com.neotee.ecommercesystem.domainprimitives.DeliveryId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.client.domain.Client;
import com.neotee.ecommercesystem.shopsystem.core.AggregateRoot;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.model.DeliveryPackage;
import com.neotee.ecommercesystem.shopsystem.order.domain.Order;
import com.neotee.ecommercesystem.usecases.ClientType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery extends AggregateRoot<DeliveryId> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client deliveryRecipient;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "delivery_id")
    private List<DeliveryContent> contents;

    protected Delivery(DeliveryId deliveryId, Order order, Client deliveryRecipient) {
        this.id = deliveryId;
        this.order = order;
        this.deliveryRecipient = deliveryRecipient;
        this.contents = new ArrayList<>();
    }

    public static Delivery create(Order order, Client deliveryRecipient) {
        if (order == null)
            throw new DomainValidationException("Delivery", "Order darf nicht null sein.");
        if (deliveryRecipient == null)
            throw new DomainValidationException("Delivery", "Delivery Recipient darf nicht null sein.");

        return new Delivery(DeliveryId.newId(), order, deliveryRecipient);
    }

    public void addPackage(DeliveryPackage deliveryPackage) {
        if (deliveryPackage == null)
            throw new DomainValidationException("Delivery", "Delivery Package darf nicht null sein.");

        contents.add(DeliveryContent.create(deliveryPackage));
    }

    public List<DeliveryPackage> getDeliveryPackages() {
        return contents.stream().map(DeliveryContent::getDeliveryPackage).toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (Delivery) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
