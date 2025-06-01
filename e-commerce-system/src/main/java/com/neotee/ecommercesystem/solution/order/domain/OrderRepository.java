package com.neotee.ecommercesystem.solution.order.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends CrudRepository<Order, OrderId> {
    @Override
    List<Order> findAll();
}
