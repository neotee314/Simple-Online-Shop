package com.neotee.ecommercesystem.solution.deliverypackage.domain;

import com.neotee.ecommercesystem.solution.order.domain.OrderId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeliveryPackageRepository extends CrudRepository<DeliveryPackage, DeliveryPackageId> {

    @Override
    List<DeliveryPackage> findAll();
}
