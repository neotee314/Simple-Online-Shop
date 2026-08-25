package com.neotee.ecommercesystem.shopsystem.order.domain;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.OrderId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<Order, OrderId> {
    @Override
    List<Order> findAll();

    List<Order> findByClientEmail(Email clientEmail);
}
