package com.neotee.ecommercesystem.shopsystem.deliverypackage.domain;

import com.neotee.ecommercesystem.shopsystem.core.AbstractEntity;
import com.neotee.ecommercesystem.domainprimitives.DeliveryPackagePartId;
import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DeliveryPackagePart extends AbstractEntity<DeliveryPackagePartId> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int quantity;

    protected DeliveryPackagePart(DeliveryPackagePartId partId) {
        this.id = partId;
    }

    protected DeliveryPackagePart(DeliveryPackagePartId partId, Product product, int quantity) {
        this.id = partId;
        this.product = product;
        this.quantity = quantity;
    }

    public static DeliveryPackagePart create(Product product, int quantity) {
        if (product == null)
            throw new DomainValidationException("DeliveryPackagePart", "Product darf nicht null sein.");
        if (quantity <= 0)
            throw new DomainValidationException("DeliveryPackagePart", "Quantity muss größer als 0 sein.");

        return new DeliveryPackagePart(DeliveryPackagePartId.newId(), product, quantity);
    }

    public void increaseQuantity(int quantity) {
        if (quantity <= 0)
            throw new DomainValidationException("DeliveryPackagePart", "Quantity muss größer als 0 sein.");
        this.quantity += quantity;
    }

    public void decreaseQuantity(int quantity) {
        if (quantity <= 0)
            throw new DomainValidationException("DeliveryPackagePart", "Quantity muss größer als 0 sein.");
        if (this.quantity - quantity < 0)
            throw new DomainValidationException("DeliveryPackagePart", "Kann nicht mehr entfernen als vorhanden ist.");
        this.quantity -= quantity;
    }

    public ProductId getProductId() {
        return product.getId();
    }

    public boolean contains(ProductId productId) {
        if (productId == null)
            throw new DomainValidationException("DeliveryPackagePart", "Product ID darf nicht null sein.");
        return this.product.getId().equals(productId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (DeliveryPackagePart) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}