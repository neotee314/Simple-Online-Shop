package com.neotee.ecommercesystem.solution.delivery.application.service;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.solution.delivery.domain.Delivery;
import com.neotee.ecommercesystem.usecases.ClientType;
import com.neotee.ecommercesystem.usecases.DeliveryUseCases;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class DeliveryUseCaseService implements DeliveryUseCases {

    private final DeliveryService deliveryService;

    public DeliveryUseCaseService(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @Override
    public UUID triggerDelivery(ClientType deliveryRecipient, Map<UUID, Integer> deliveryContent) {
        validateRecipient(deliveryRecipient);
        validateContent(deliveryContent);

        Delivery delivery = deliveryService.createDelivery(deliveryRecipient, deliveryContent);
        return delivery.getId();
    }

    @Override
    public Map<UUID, Integer> getDeliveryHistory(EmailType clientEmail) {
        if (clientEmail == null || Strings.isBlank(clientEmail.toString())) {
            throw new ShopException("Client email cannot be null or empty");
        }

        return deliveryService.getDeliveryHisotry((Email) clientEmail);
    }

    @Override
    public void deleteDeliveryHistory() {
        deliveryService.deleteDeliveryHisotry();
    }

    private void validateRecipient(ClientType recipient) {
        if (recipient == null) {
            throw new ShopException("Recipient cannot be null.");
        }
        if (Strings.isBlank(recipient.getName())
                || recipient.getHomeAddress() == null
                || Strings.isBlank(recipient.getHomeAddress().getCity())
                || Strings.isBlank(recipient.getHomeAddress().getStreet())
                || recipient.getHomeAddress().getZipCode() == null
                || Strings.isBlank(recipient.getHomeAddress().getZipCode().toString())
                || recipient.getEmail() == null
                || Strings.isBlank(recipient.getEmail().toString())
        ) {
            throw new ShopException("Recipient has invalid fields.");
        }
    }

    private void validateContent(Map<UUID, Integer> content) {
        if (content == null || content.isEmpty()) {
            throw new ShopException("Delivery content is empty or null.");
        }
        int totalGoods = content.values().stream().mapToInt(Integer::intValue).sum();
        if (totalGoods > 20) {
            throw new ShopException("Cannot deliver more than 20 goods at once.");
        }
    }
}
