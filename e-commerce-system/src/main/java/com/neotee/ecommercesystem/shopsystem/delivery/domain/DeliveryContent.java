package com.neotee.ecommercesystem.shopsystem.delivery.domain;

import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter

public class DeliveryContent {
    @Id
    private DeliveryContentId deliveryContentId;

    @ManyToOne
    private Product product;

    private Integer quantity;

    public DeliveryContent(){
        this.deliveryContentId = new DeliveryContentId();
    }

    public void addToQuantity(Integer quantity) {
        this.quantity += quantity;
    }
}
