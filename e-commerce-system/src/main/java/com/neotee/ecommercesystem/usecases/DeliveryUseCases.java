package com.neotee.ecommercesystem.usecases;


import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;

import java.util.Map;
import java.util.UUID;

/**
 * This interface contains methods needed in the context of use cases dealing with logistics,
 * i.e. the delivery of goods to a client. It is probably incomplete, and will grow over time.
 */
public interface DeliveryUseCases {
    /**
     * Delivers a good to a client. The good is identified by its id, and the client by
     * his/her name, street, city and postal code.
     * @param deliveryRecipient
     * @param deliveryContent - a map of good ids and quantities
     * @return the id of the delivery, if successfully triggered
     * @throws ShopException if ...
     *      - deliveryRecipient is null
     *      - any of the properties in deliveryRecipient (the getXxx(...) methods) return null or empty strings
     *      - deliveryContent is null or empty
     *      - the total number of goods in the delivery is > 20
     */
    public UUID triggerDelivery( ClientType deliveryRecipient, Map<UUID, Integer> deliveryContent );


    /**
     * Returns a map showing which goods have been delivered to a client, and how many of each good
     *
     * @param clientEmail
     * @return the delivery history of the client (map is empty if the client has not had any deliveries yet)
     * @throws ShopException if
     *      - email is null
     *      - the client with the given email does not exist
     */
    public Map<UUID, Integer> getDeliveryHistory( EmailType clientEmail );



    /**
     *  Deletes all delivery history.
     */
    public void deleteDeliveryHistory();
}
