package com.neotee.ecommercesystem.shopsystem.deliverypackage.domain;


import com.neotee.ecommercesystem.shopsystem.thing.domain.Thing;
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
    private Thing thing;
    private int quantity;

    public DeliveryPackagePart(Thing thing, int quantity) {
        this.id = new DeliveryPackagePartId();
        this.thing = thing;
        this.quantity = quantity;
    }

    public UUID getThingId() {
        return thing.getThingId().getId();
    }
}
