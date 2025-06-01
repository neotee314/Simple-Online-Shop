package com.neotee.ecommercesystem.shopsystem.storageunit.domain;

import com.neotee.ecommercesystem.GenericId;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class StorageUnitId extends GenericId {
    protected StorageUnitId() {super();}
    public StorageUnitId(UUID id) {super(id);}
}
