package com.neotee.ecommercesystem.solution.delivery.domain;

import com.neotee.ecommercesystem.GenericId;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class DeliveryId extends GenericId {
    protected DeliveryId() { super(); }
    public DeliveryId(UUID id) { super(id); }
}
