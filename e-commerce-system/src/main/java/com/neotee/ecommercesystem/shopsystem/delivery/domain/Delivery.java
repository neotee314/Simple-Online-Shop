package com.neotee.ecommercesystem.shopsystem.delivery.domain;

import com.neotee.ecommercesystem.domainprimitives.Email;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Delivery {
    @Id
    private UUID id = UUID.randomUUID();

    @Embedded
    private Email clientEmail;


    @ElementCollection
    private Map<UUID, Integer> deliveryContents = new HashMap<>();

    public Delivery(Email clientEmail) {
        this.clientEmail = clientEmail;
    }

    public void addToContent(Map.Entry<UUID, Integer> deliveryContent) {
        UUID goodId = deliveryContent.getKey();
        Integer quantity = deliveryContent.getValue();
        Integer currentQuantity = deliveryContents.getOrDefault(goodId, 0);
        deliveryContents.put(goodId, currentQuantity + quantity);
    }
}