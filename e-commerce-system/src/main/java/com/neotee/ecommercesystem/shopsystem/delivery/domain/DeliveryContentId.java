package com.neotee.ecommercesystem.shopsystem.delivery.domain;

import com.neotee.ecommercesystem.GenericId;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class DeliveryContentId extends GenericId {
    protected DeliveryContentId() { super(); }
    public DeliveryContentId(UUID id) { super(id); }
}
