package com.neotee.ecommercesystem.anticorruption;


import com.neotee.ecommercesystem.shopsystem.delivery.application.service.DeliveryService;
import com.neotee.ecommercesystem.usecases.ClientType;
import com.neotee.ecommercesystem.usecases.DeliveryUseCases;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class DeliveryUseCaseService implements DeliveryUseCases {

    private final DeliveryService deliveryService;

    public DeliveryUseCaseService(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }


    @Override
    public UUID triggerDelivery(ClientType deliveryRecipient, Map<UUID, Integer> deliveryContent) {
        return null;
    }

    @Override
    public Map<UUID, Integer> getDeliveryHistory(EmailType clientEmail) {
        return Map.of();
    }

    @Override
    public void deleteDeliveryHistory() {

    }
}
