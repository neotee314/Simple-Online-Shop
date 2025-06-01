package com.neotee.ecommercesystem.solution.storageunit.domain;

import com.neotee.ecommercesystem.GenericId;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class StockLevelId extends GenericId {
    protected StockLevelId() {super();}
    public StockLevelId(UUID id) {super(id);}
}
