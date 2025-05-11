package com.neotee.ecommercesystem.solution.delivery.application;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.HomeAddress;
import com.neotee.ecommercesystem.solution.delivery.domain.Delivery;
import com.neotee.ecommercesystem.solution.delivery.domain.DeliveryRepository;
import com.neotee.ecommercesystem.usecases.ClientType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Map;
import java.util.UUID;

@Service
public class DeliveryService {
    @Autowired
    private DeliveryRepository deliveryRepository;

    public Delivery createDelivery(ClientType clientType, Map<UUID, Integer> deliveryContent) {
        if (clientType == null || deliveryContent == null) throw new ShopException("invalid data");

        Email email = (Email) clientType.getEmail();

        Delivery delivery = deliveryRepository.findByClientEmail(email);
        if (delivery == null) {
            delivery = new Delivery(email);
            deliveryContent.entrySet().forEach(delivery::addToContent);
        } else {
            deliveryContent.entrySet().forEach(delivery::addToContent);
        }
        deliveryRepository.save(delivery);

        return delivery;

    }

    public Map<UUID, Integer> getDeliveryHisotry(Email clientEmail) {
        if (clientEmail == null) throw new ShopException("client cannot be null");
        Delivery delivery = deliveryRepository.findByClientEmail(clientEmail);
        Map<UUID, Integer> delvieryHistory = delivery.getDeliveryContents();
        return delvieryHistory;
    }

    public void deleteDeliveryHisotry() {
        deliveryRepository.deleteAll();
    }
}
