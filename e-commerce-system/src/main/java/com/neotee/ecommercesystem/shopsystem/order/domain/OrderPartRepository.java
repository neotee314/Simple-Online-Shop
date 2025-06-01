package com.neotee.ecommercesystem.shopsystem.order.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderPartRepository extends CrudRepository<OrderPart, OrderPartId> {
}
