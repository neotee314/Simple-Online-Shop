package com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.model;

import com.neotee.ecommercesystem.domainprimitives.*;
import com.neotee.ecommercesystem.shopsystem.core.AggregateRoot;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.order.domain.Order;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.model.StorageUnit;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DeliveryPackage extends AggregateRoot<DeliveryPackageId> {

    @ManyToOne
    @JoinColumn(name = "storage_unit_id", nullable = false)
    private StorageUnit storageUnit;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "delivery_package_id")
    private List<DeliveryPackagePart> parts;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DeliveryPackageStatus status;

    protected DeliveryPackage(DeliveryPackageId packageId) {
        this.id = packageId;
        this.parts = new ArrayList<>();
    }

    protected DeliveryPackage(DeliveryPackageId packageId, StorageUnit storageUnit, Order order) {
        this.id = packageId;
        this.storageUnit = storageUnit;
        this.order = order;
        this.parts = new ArrayList<>();
        this.status = DeliveryPackageStatus.NOT_SHIPPED;
    }

    public static DeliveryPackage create(StorageUnit storageUnit, Order order) {
        if (storageUnit == null)
            throw new DomainValidationException("DeliveryPackage", "Storage Unit darf nicht null sein.");
        if (order == null)
            throw new DomainValidationException("DeliveryPackage", "Order darf nicht null sein.");

        return new DeliveryPackage(DeliveryPackageId.newId(), storageUnit, order);
    }

    public static DeliveryPackage create(DeliveryPackageId packageId, StorageUnit storageUnit, Order order) {
        if (packageId == null)
            throw new DomainValidationException("DeliveryPackage", "Package ID darf nicht null sein.");
        if (storageUnit == null)
            throw new DomainValidationException("DeliveryPackage", "Storage Unit darf nicht null sein.");
        if (order == null)
            throw new DomainValidationException("DeliveryPackage", "Order darf nicht null sein.");

        return new DeliveryPackage(packageId, storageUnit, order);
    }

    public void addPart(Product product, int quantity) {
        if (product == null)
            throw new DomainValidationException("DeliveryPackage", "Product darf nicht null sein.");
        if (quantity <= 0)
            throw new DomainValidationException("DeliveryPackage", "Quantity muss größer als 0 sein.");

        var existingPart = findPartByProduct(product);
        if (existingPart != null) {
            existingPart.increaseQuantity(quantity);
            return;
        }

        var newPart = DeliveryPackagePart.create(product, quantity);
        parts.add(newPart);
    }


    public boolean isEmpty() {
        return parts.isEmpty();
    }

    public int getTotalQuantity() {
        return parts.stream()
                .mapToInt(DeliveryPackagePart::getQuantity)
                .sum();
    }

    public int getPartCount() {
        return parts.size();
    }

    public void updateStatus(DeliveryPackageStatus status) {
        if (status == null)
            throw new DomainValidationException("DeliveryPackage", "Status darf nicht null sein.");

        if (this.status == DeliveryPackageStatus.NOT_SHIPPED && status == DeliveryPackageStatus.DELIVERED)
            throw new DomainValidationException("DeliveryPackage", "Package muss zuerst in den Versand gehen.");

        if (this.status == DeliveryPackageStatus.DELIVERED)
            throw new DomainValidationException("DeliveryPackage", "Ein zugestelltes Package kann nicht erneut versendet werden.");

        this.status = (DeliveryPackageStatus) status;
    }

    public boolean contains(Product product) {
        if (product == null)
            throw new DomainValidationException("DeliveryPackage", "Product darf nicht null sein.");
        return findPartByProduct(product) != null;
    }


    public StorageUnitId getStorageUnitId() {
        return storageUnit.getId();
    }


    public Map<ProductId, Integer> getItems() {
        return parts.stream()
                .collect(Collectors.toMap(part -> part.getProduct().getId(), DeliveryPackagePart::getQuantity));
    }

    private DeliveryPackagePart findPartByProduct(Product product) {
        return parts.stream()
                .filter(part -> part.getProduct().equals(product))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (DeliveryPackage) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}