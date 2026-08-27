package com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.service;

import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.model.DeliveryPackage;
import com.neotee.ecommercesystem.shopsystem.order.domain.Order;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.model.StorageUnit;

import java.util.List;

public interface DeliveryPackageAllocationServiceInterface {

    List<DeliveryPackage> allocateDeliveryPackageToOrder(Order order, List<StorageUnit> storageUnits);
}