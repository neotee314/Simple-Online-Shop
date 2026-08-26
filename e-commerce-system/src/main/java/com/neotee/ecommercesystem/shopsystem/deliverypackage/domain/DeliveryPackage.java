package com.neotee.ecommercesystem.shopsystem.deliverypackage.domain;

import com.neotee.ecommercesystem.shopsystem.core.AggregateRoot;
import com.neotee.ecommercesystem.domainprimitives.DeliveryPackageId;
import com.neotee.ecommercesystem.domainprimitives.StorageUnitId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.order.domain.Order;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnit;
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "delivery_package_id")
    private List<DeliveryPackagePart> parts;

    protected DeliveryPackage(DeliveryPackageId packageId) {
        this.id = packageId;
        this.parts = new ArrayList<>();
    }

    protected DeliveryPackage(DeliveryPackageId packageId, StorageUnit storageUnit, Order order) {
        this.id = packageId;
        this.storageUnit = storageUnit;
        this.order = order;
        this.parts = new ArrayList<>();
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

    public void addParts(Map<Product, Integer> items) {
        if (items == null || items.isEmpty())
            throw new DomainValidationException("DeliveryPackage", "Items dürfen nicht leer sein.");

        items.forEach(this::addPart);
    }

    public void removePart(Product product) {
        if (product == null)
            throw new DomainValidationException("DeliveryPackage", "Product darf nicht null sein.");

        var part = findPartByProduct(product);
        if (part != null) {
            parts.remove(part);
        }
    }

    public void removePart(Product product, int quantity) {
        if (product == null)
            throw new DomainValidationException("DeliveryPackage", "Product darf nicht null sein.");
        if (quantity <= 0)
            throw new DomainValidationException("DeliveryPackage", "Quantity muss größer als 0 sein.");

        var part = findPartByProduct(product);
        if (part == null)
            throw new DomainValidationException("DeliveryPackage", "Product nicht im Lieferpaket gefunden.");

        var newQuantity = part.getQuantity() - quantity;
        if (newQuantity > 0) {
            part.decreaseQuantity(quantity);
        } else if (newQuantity == 0) {
            parts.remove(part);
        } else {
            throw new DomainValidationException("DeliveryPackage", "Kann nicht mehr entfernen als vorhanden ist.");
        }
    }

    public void clear() {
        parts.clear();
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

    public boolean contains(Product product) {
        if (product == null)
            throw new DomainValidationException("DeliveryPackage", "Product darf nicht null sein.");
        return findPartByProduct(product) != null;
    }

    public boolean hasStorageUnit(StorageUnitId storageUnitId) {
        if (storageUnitId == null)
            throw new DomainValidationException("DeliveryPackage", "Storage Unit ID darf nicht null sein.");
        return this.storageUnit.getId().equals(storageUnitId);
    }

    public StorageUnitId getStorageUnitId() {
        return storageUnit.getId();
    }


    public Map<Product, Integer> getItemsAsProductMap() {
        return parts.stream()
                .collect(Collectors.toMap(
                        DeliveryPackagePart::getProduct,
                        DeliveryPackagePart::getQuantity
                ));
    }

    public Map<UUID, Integer> getItemsAsUuidMap() {
        return parts.stream()
                .collect(Collectors.toMap(
                        part -> part.getProduct().getId().getId(),
                        DeliveryPackagePart::getQuantity
                ));
    }

    private DeliveryPackagePart findPartByProduct(Product product) {
        return parts.stream()
                .filter(part -> part.getProduct().getId().equals(product.getId()))
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