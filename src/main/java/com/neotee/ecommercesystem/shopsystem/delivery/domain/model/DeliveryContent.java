package com.neotee.ecommercesystem.shopsystem.delivery.domain.model;

import com.neotee.ecommercesystem.domainprimitives.DeliveryContentId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.core.AbstractEntity;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.model.DeliveryPackage;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryContent extends AbstractEntity<DeliveryContentId> {

    @ManyToOne
    @JoinColumn(name = "delivery_package_id", nullable = false)
    private DeliveryPackage deliveryPackage;

    protected DeliveryContent(DeliveryContentId id, DeliveryPackage deliveryPackage) {
        this.id = id;
        this.deliveryPackage = deliveryPackage;
    }

    public static DeliveryContent create(DeliveryPackage deliveryPackage) {
        if (deliveryPackage == null)
            throw new DomainValidationException("DeliveryContent", "Delivery Package darf nicht null sein.");

        return new DeliveryContent(DeliveryContentId.newId(), deliveryPackage);
    }
}