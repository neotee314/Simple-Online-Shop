package com.neotee.ecommercesystem.solution.deliverypackage.domain;

import com.neotee.ecommercesystem.ShopException;
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
    private UUID deliveryId = UUID.randomUUID();

    private UUID storageUnitId;
    private UUID orderId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryPackagePart> parts = new ArrayList<>();

    public DeliveryPackage(UUID storageUnitId, UUID orderId) {
        if (storageUnitId == null || orderId == null) throw new ShopException("Invalid storage unit ID or order ID");
        this.storageUnitId = storageUnitId;
        this.orderId = orderId;
    }


    public boolean hasStorage(UUID storageUnitId) {
        if(storageUnitId == null) throw new ShopException("Storage unit ID must not be null");
        return this.storageUnitId.equals(storageUnitId);
    }

    public Map<UUID, Integer> createParts(Map<UUID, Integer> inputItems) {
        Map<UUID, Integer> usedItems = new HashMap<>();

        Map<UUID, Integer> itemsCopy = new HashMap<>(inputItems);

        for (Map.Entry<UUID, Integer> entry : itemsCopy.entrySet()) {
            UUID thingId = entry.getKey();
            int quantity = entry.getValue();

            parts.add(new DeliveryPackagePart(thingId, quantity));
            usedItems.put(thingId, quantity);
        }

        return usedItems;
    }
}
