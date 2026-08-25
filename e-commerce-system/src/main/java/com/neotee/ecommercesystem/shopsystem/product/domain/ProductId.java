package com.neotee.ecommercesystem.shopsystem.product.domain;

import com.neotee.ecommercesystem.domainprimitives.GenericId;
import jakarta.persistence.Embeddable;


import java.util.UUID;

@Embeddable
public class ProductId extends GenericId {
    protected ProductId() { super(); }
    public ProductId(UUID id) { super(id); }


}
