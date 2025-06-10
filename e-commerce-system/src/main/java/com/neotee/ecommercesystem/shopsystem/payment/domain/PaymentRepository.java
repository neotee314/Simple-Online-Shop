package com.neotee.ecommercesystem.shopsystem.payment.domain;

import com.neotee.ecommercesystem.domainprimitives.Email;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, PaymentId> {

    List<Payment> findByClientEmail(Email clientEmail);
}
