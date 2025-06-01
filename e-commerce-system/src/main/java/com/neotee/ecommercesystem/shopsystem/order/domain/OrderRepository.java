package com.neotee.ecommercesystem.shopsystem.order.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<Order, OrderId> {
    @Override
    List<Order> findAll();
}
