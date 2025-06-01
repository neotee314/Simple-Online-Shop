package com.neotee.ecommercesystem.shopsystem.payment.domain;

import com.neotee.ecommercesystem.GenericId;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class PaymentId extends GenericId {
    protected PaymentId() { super(); }
    public PaymentId(UUID id) { super(id); }
}
