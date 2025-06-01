package com.neotee.ecommercesystem.shopsystem.order.domain;

import com.neotee.ecommercesystem.GenericId;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class OrderId extends GenericId {
    protected OrderId() { super(); }
    public OrderId(UUID id) { super(id); }
}
