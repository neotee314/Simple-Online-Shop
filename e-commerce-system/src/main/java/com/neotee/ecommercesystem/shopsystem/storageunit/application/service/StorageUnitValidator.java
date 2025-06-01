package com.neotee.ecommercesystem.shopsystem.storageunit.application.service;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.HomeAddressType;

import java.util.UUID;

public class StorageUnitValidator {

    public static void validateStorageUnitId(UUID storageUnitId) {
        if (storageUnitId == null) {
            throw new ShopException("storageUnitId must not be null");
        }
    }


    public static void validateQuantityNotNegative(int quantity) {
        if (quantity < 0) {
            throw new ShopException( " must not be negative");
        }
    }

    public static void validateNewStorageUnitData(Object address, String name) {
        if (address == null || name == null || name.isEmpty()) {
            throw new ShopException("Invalid storage unit data: address and name must be provided");
        }
    }

    public static void validateNewStorageUnit(HomeAddressType address, String name) {
        if (address == null || name == null || name.isEmpty()) {
            throw new ShopException("Invalid address or name");
        }
    }
}
