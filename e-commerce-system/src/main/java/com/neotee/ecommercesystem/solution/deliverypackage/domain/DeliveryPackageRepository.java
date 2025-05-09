package com.neotee.ecommercesystem.solution.deliverypackage.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeliveryPackageRepository extends CrudRepository<DeliveryPackage, UUID> {
    List<DeliveryPackage> findByOrderId(UUID orderId);
}
