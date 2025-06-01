package com.neotee.ecommercesystem.shopsystem.deliverypackage.domain;

import com.neotee.ecommercesystem.GenericId;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class DeliveryPackagePartId extends GenericId {
    protected DeliveryPackagePartId() { super(); }
    public DeliveryPackagePartId(UUID id) { super(id); }
}
