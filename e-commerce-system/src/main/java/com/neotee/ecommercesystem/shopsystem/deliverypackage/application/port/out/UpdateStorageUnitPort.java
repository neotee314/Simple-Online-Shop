package com.neotee.ecommercesystem.shopsystem.deliverypackage.application.port.out;

import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnit;

public interface UpdateStorageUnitPort {
    void update(StorageUnit storageUnit);
}