package com.neotee.ecommercesystem.shopsystem.deliverypackage.domain;

import com.neotee.ecommercesystem.domainprimitives.DeliveryPackageId;
import com.neotee.ecommercesystem.domainprimitives.OrderId;
import com.neotee.ecommercesystem.domainprimitives.StorageUnitId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryPackageRepository extends CrudRepository<DeliveryPackage, DeliveryPackageId> {

    @Override
    List<DeliveryPackage> findAll();

    List<DeliveryPackage> findByOrderId(OrderId orderId);

    Optional<DeliveryPackage> findByOrderIdAndStorageUnitId(OrderId orderId, StorageUnitId storageUnitId);
}
