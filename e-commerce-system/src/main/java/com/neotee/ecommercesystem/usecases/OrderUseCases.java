package com.neotee.ecommercesystem.usecases;


import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;

import java.util.Map;
import java.util.UUID;

/**
 * This interface contains methods needed in the context of the order history of a client.
 */
public interface OrderUseCases {
    /**
     * Returns a map showing which things have been bought by a client, and how many of each thing
     *
     * @param clientEmail
     * @return the order history of the client (map is empty if the client has not bought anything yet)
     * @throws ShopException if
     *      - the email is null
     *      - the client with the given email does not exist
     */
    public Map<UUID, Integer> getOrderHistory( EmailType clientEmail );



    /**
     * Deletes all orders in the system
     */
    public void deleteAllOrders();
}
