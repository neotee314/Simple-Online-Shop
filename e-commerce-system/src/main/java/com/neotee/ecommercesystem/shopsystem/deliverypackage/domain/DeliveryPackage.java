package com.neotee.ecommercesystem.shopsystem.deliverypackage.domain;

import com.neotee.ecommercesystem.exception.ShopException;
import com.neotee.ecommercesystem.shopsystem.order.domain.Order;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import jakarta.persistence.OneToMany;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DeliveryPackage {
    @Id
    private DeliveryPackageId id;

    @ManyToOne
    private StorageUnit storageUnit;

    @ManyToOne
    private Order order;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryPackagePart> parts = new ArrayList<>();

    public DeliveryPackage(StorageUnit storageUnit, Order order) {
        if (storageUnit == null || order == null) throw new ShopException("Invalid storage unit ID or order ID");
        this.id = new DeliveryPackageId();
        this.storageUnit = storageUnit;
        this.order = order;
    }


    public boolean hasStorage(UUID storageUnitId) {
        if(storageUnitId == null) throw new ShopException("Storage unit ID must not be null");
        return this.storageUnit.getStorageId().getId().equals(storageUnitId);
    }

    public Map<Product, Integer> createParts(Map<Product, Integer> inputItems) {
        Map<Product, Integer> usedItems = new HashMap<>();

        Map<Product, Integer> itemsCopy = new HashMap<>(inputItems);

        for (Map.Entry<Product, Integer> entry : itemsCopy.entrySet()) {
            Product productId = entry.getKey();
            int quantity = entry.getValue();

            parts.add(new DeliveryPackagePart(productId, quantity));
            usedItems.put(productId, quantity);
        }

        return usedItems;
    }

    public UUID getStorageUnitId() {
        return storageUnit.getStorageId().getId();
    }
}
