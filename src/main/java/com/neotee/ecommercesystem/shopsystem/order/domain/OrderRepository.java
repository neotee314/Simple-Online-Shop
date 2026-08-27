package com.neotee.ecommercesystem.shopsystem.order.domain;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.OrderId;
import com.neotee.ecommercesystem.shopsystem.client.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, OrderId> {

    @Override
    List<Order> findAll();

    List<Order> findByClient(Client client);

    @Query("SELECT o FROM Order o WHERE o.client.email = :email")
    List<Order> findByClientEmail(@Param("email") Email email);
}