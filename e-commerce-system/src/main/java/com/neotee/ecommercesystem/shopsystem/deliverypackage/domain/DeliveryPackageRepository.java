package com.neotee.ecommercesystem.shopsystem.deliverypackage.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryPackageRepository extends CrudRepository<DeliveryPackage, DeliveryPackageId> {

    @Override
    List<DeliveryPackage> findAll();
}
