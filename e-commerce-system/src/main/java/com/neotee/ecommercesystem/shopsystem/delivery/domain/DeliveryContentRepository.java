package com.neotee.ecommercesystem.shopsystem.delivery.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryContentRepository extends CrudRepository<DeliveryContent, DeliveryContentId> {
}
