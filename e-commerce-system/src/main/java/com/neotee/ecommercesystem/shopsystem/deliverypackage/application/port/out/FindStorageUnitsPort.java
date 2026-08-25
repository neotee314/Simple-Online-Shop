package com.neotee.ecommercesystem.shopsystem.deliverypackage.application.port.out;

import com.neotee.ecommercesystem.domainprimitives.StorageUnitId;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnit;

import java.util.List;

public interface FindStorageUnitsPort {
    List<StorageUnit> findAll();

    StorageUnit findById(StorageUnitId storageUnitId);
}