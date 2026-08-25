package com.neotee.ecommercesystem.shopsystem.deliverypackage.domain;


import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DeliveryPackagePart {
    @Id
    private DeliveryPackagePartId id;

    @ManyToOne
    private Product product;
    private int quantity;

    public DeliveryPackagePart(Product product, int quantity) {
        this.id = new DeliveryPackagePartId();
        this.product = product;
        this.quantity = quantity;
    }

    public UUID getThingId() {
        return product.getProductId().getId();
    }
}
