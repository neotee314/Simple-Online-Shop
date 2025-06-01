package com.neotee.ecommercesystem.solution.deliverypackage.domain;

import com.neotee.ecommercesystem.GenericId;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class DeliveryPackageId extends GenericId {
    protected DeliveryPackageId() { super(); }
    public DeliveryPackageId(UUID id) { super(id); }
}
