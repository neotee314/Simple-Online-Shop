package com.neotee.ecommercesystem.shopsystem.delivery.application.service;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.exception.EntityNotFoundException;
import com.neotee.ecommercesystem.shopsystem.delivery.domain.*;
import com.neotee.ecommercesystem.usecases.ClientType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryContentRepository deliveryContentRepository;

    public Delivery createDelivery(ClientType clientType, Map<UUID, Integer> deliveryContent) {
        if (clientType == null || deliveryContent == null) throw new EntityNotFoundException();

        Email email = (Email) clientType.getEmail();

        Delivery delivery = deliveryRepository.findByClientEmail(email);
        if (delivery == null) {
            delivery = new Delivery(email);
            addToDeliveryContent(delivery, deliveryContent);
            return delivery;
        }
        addToDeliveryContent(delivery, deliveryContent);
        return delivery;

    }

    private void addToDeliveryContent(Delivery delivery, Map<UUID, Integer> deliveryContent) {
        for (UUID id : deliveryContent.keySet()) {
            DeliveryContentId deliveryContentId = new DeliveryContentId(id);
            DeliveryContent content = deliveryContentRepository.findById(deliveryContentId).orElseThrow(EntityNotFoundException::new);
            delivery.addToContent(content);
            deliveryRepository.save(delivery);
        }
    }

    public Map<UUID, Integer> getDeliveryHistory(Email clientEmail) {
        if (clientEmail == null) throw new ShopException("client cannot be null");
        Delivery delivery = deliveryRepository.findByClientEmail(clientEmail);
        return delivery.getDliveryContentAsMapValue();
    }

    public void deleteDeliveryHistory() {
        deliveryRepository.deleteAll();
    }
}
