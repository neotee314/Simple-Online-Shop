package com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.repository;

import com.neotee.ecommercesystem.domainprimitives.DeliveryPackageId;
import com.neotee.ecommercesystem.domainprimitives.OrderId;
import com.neotee.ecommercesystem.domainprimitives.StorageUnitId;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.model.DeliveryPackage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryPackageRepository extends CrudRepository<DeliveryPackage, DeliveryPackageId> {

    @Override
    List<DeliveryPackage> findAll();

    @Query("SELECT d FROM DeliveryPackage d WHERE d.order.id = :orderId")
    List<DeliveryPackage> findByOrderId(OrderId orderId);

    @Query("SELECT d FROM DeliveryPackage d WHERE d.order.id = :orderId AND d.storageUnit.id = :storageUnitId")
    Optional<DeliveryPackage> findByOrderIdAndStorageUnitId(
            @Param("orderId") OrderId orderId,
            @Param("storageUnitId") StorageUnitId storageUnitId
    );
    //Optional<DeliveryPackage> findByOrderIdAndStorageUnitId(OrderId orderId, StorageUnitId storageUnitId);
}
