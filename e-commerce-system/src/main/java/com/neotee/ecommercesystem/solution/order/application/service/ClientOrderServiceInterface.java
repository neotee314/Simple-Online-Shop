package com.neotee.ecommercesystem.solution.order.application.service;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.ZipCode;

import java.util.List;
import java.util.UUID;

public interface ClientOrderServiceInterface {
    ZipCode findClientZipCode(Email clientEmail);

    void addToOrderHistory(Email clientEmail, UUID orderId);

    List<UUID> getOrderHistory(Email clientEmail);
}
