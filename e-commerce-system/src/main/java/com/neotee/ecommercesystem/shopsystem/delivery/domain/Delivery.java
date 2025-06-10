package com.neotee.ecommercesystem.shopsystem.delivery.domain;

import com.neotee.ecommercesystem.domainprimitives.Email;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Delivery {
    @Id
    private DeliveryId deliveryId;

    @Embedded
    private Email clientEmail;


    @OneToMany(cascade = CascadeType.ALL)
    private List<DeliveryContent> deliveryContents;

    public Delivery(Email clientEmail) {
        this.deliveryId = new DeliveryId();
        this.deliveryContents = new ArrayList<>();
        this.clientEmail = clientEmail;
    }

    public void addToContent(DeliveryContent deliveryContent) {
        DeliveryContent found = getDeliveryContent(deliveryContent.getDeliveryContentId());
        if (found == null) {
            deliveryContents.add(deliveryContent);
            return;
        }
        found.addToQuantity(deliveryContent.getQuantity());
    }


    private DeliveryContent getDeliveryContent(DeliveryContentId deliveryContentId) {
        for(DeliveryContent deliveryContent : deliveryContents) {
            if(deliveryContent.getDeliveryContentId().equals(deliveryContentId)) {
                return deliveryContent;
            }
        }
        return null;
    }

    public Map<UUID, Integer> getDliveryContentAsMapValue() {
        Map<UUID, Integer> map = new HashMap<>();
        for(DeliveryContent deliveryContent : deliveryContents) {
             map.put(deliveryContent.getDeliveryContentId().getId(), deliveryContent.getQuantity());
        }
        return map;
    }
}