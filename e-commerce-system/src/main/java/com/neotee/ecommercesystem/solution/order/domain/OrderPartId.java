package com.neotee.ecommercesystem.solution.order.domain;

import com.neotee.ecommercesystem.GenericId;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class OrderPartId extends GenericId {
    protected OrderPartId() { super(); }
    public OrderPartId(UUID id) { super(id); }
}
