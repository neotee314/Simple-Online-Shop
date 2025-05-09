package com.neotee.ecommercesystem.solution.deliverypackage.domain;


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
    private UUID id = UUID.randomUUID();
    private UUID thingId;
    private int quantity;

    public DeliveryPackagePart(UUID thingId, int quantity) {
        this.thingId = thingId;
        this.quantity = quantity;
    }
}
