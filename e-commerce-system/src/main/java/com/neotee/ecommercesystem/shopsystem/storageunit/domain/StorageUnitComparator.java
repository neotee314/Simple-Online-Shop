package com.neotee.ecommercesystem.shopsystem.storageunit.domain;

import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.shopsystem.thing.domain.Thing;

import java.util.Comparator;
import java.util.Map;


public class StorageUnitComparator implements Comparator<StorageUnit> {
    private final Map<Thing, Integer> unfulfilledItems;
    private final ZipCode clientZipCode;

    public StorageUnitComparator(Map<Thing, Integer> unfulfilledItems, ZipCode clientZipCode) {
        this.unfulfilledItems = unfulfilledItems;
        this.clientZipCode = clientZipCode;
    }

    @Override
    public int compare(StorageUnit su1, StorageUnit su2) {
        int contribCompare = Integer.compare(
                su2.getTotalContributingItems(unfulfilledItems),
                su1.getTotalContributingItems(unfulfilledItems)
        );
        if (contribCompare != 0) {
            return contribCompare;
        }
        return Integer.compare(
                su1.getDistanceToClient(clientZipCode),
                su2.getDistanceToClient(clientZipCode)
        );
    }
}

