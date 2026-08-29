package com.neotee.ecommercesystem.shopsystem.delivery.domain.repository;

import com.neotee.ecommercesystem.domainprimitives.DeliveryId;
import com.neotee.ecommercesystem.shopsystem.delivery.domain.model.Delivery;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, DeliveryId> {
    List<Delivery> findByDeliveryRecipientEmail(EmailType email);
}
