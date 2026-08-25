package com.neotee.ecommercesystem.shopsystem.storageunit.domain;

import com.neotee.ecommercesystem.domainprimitives.GenericId;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class StockLevelId extends GenericId {
    protected StockLevelId() {super();}
    public StockLevelId(UUID id) {super(id);}
}
