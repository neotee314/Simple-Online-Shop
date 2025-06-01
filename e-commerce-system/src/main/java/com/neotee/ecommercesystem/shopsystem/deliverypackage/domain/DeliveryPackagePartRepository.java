package com.neotee.ecommercesystem.shopsystem.deliverypackage.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryPackagePartRepository extends CrudRepository<DeliveryPackagePart, DeliveryPackagePartId> {
}
