package com.neotee.ecommercesystem.usecases;

import com.neotee.ecommercesystem.exceptions.ShopException;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;

import java.util.List;
import java.util.UUID;

/**
 * This interface contains methods needed in the context of use cases
 * dealing with the delivery of orders to clients.
 * <p>
 * A delivery belongs to an order and consists of one or more delivery
 * packages. The delivery packages themselves are managed by
 * DeliveryPackageUseCases.
 * <p>
 * A delivery package may be delivered independently of the other packages
 * belonging to the same order.
 */
public interface DeliveryUseCases {

    /**
     * Triggers the delivery of an order to the given recipient.
     * <p>
     * The delivery consists of the delivery packages belonging to the order.
     * The packages may be delivered independently of each other.
     *
     * @param orderId           the id of the order to be delivered
     * @param deliveryRecipient the recipient of the delivery
     * @return the id of the newly created delivery
     * @throws ShopException if ...
     *                       - orderId is null
     *                       - the order with the given id does not exist
     *                       - deliveryRecipient is null
     *                       - any of the properties in deliveryRecipient return null or empty strings
     *                       - the order cannot be delivered
     */
    UUID triggerDelivery(UUID orderId, ClientType deliveryRecipient);


    /**
     * Returns the IDs of all delivery packages belonging to a delivery.
     * <p>
     * This can be used to determine which packages are part of a delivery
     * and to query their individual delivery status.
     *
     * @param deliveryId the id of the delivery
     * @return a list containing the IDs of all delivery packages belonging
     * to the delivery
     * @throws ShopException if ...
     *                       - deliveryId is null
     *                       - the delivery with the given id does not exist
     */
    List<UUID> getDeliveryPackages(UUID deliveryId);


    /**
     * Returns the current delivery status of a delivery package.
     * <p>
     * A package can be delivered independently of the other packages
     * belonging to the same order.
     *
     * @param deliveryPackageId the id of the delivery package
     * @return the current status of the delivery package
     * @throws ShopException if ...
     *                       - deliveryPackageId is null
     *                       - the delivery package with the given id does not exist
     */
    String getDeliveryPackageStatus(UUID deliveryPackageId);


    /**
     * Updates the status of a delivery package.
     * <p>
     * This allows individual packages belonging to the same order to be
     * delivered at different points in time.
     *
     * @param deliveryPackageId the id of the delivery package
     * @param status            the new delivery status
     * @throws ShopException if ...
     *                       - deliveryPackageId is null
     *                       - the delivery package with the given id does not exist
     *                       - status is null
     *                       - the requested status transition is invalid
     */
    void updateDeliveryPackageStatus(UUID deliveryPackageId, String status);


    /**
     * Returns the delivery history of a client.
     * <p>
     * The history contains the deliveries associated with the given client.
     *
     * @param clientEmail the email address of the client
     * @return a list containing the IDs of the client's deliveries.
     * @throws ShopException if ...
     *                       - clientEmail is null
     *                       - the client with the given email does not exist
     */
    List<UUID> getDeliveryHistory(EmailType clientEmail);


    /**
     * Deletes all delivery information.
     * <p>
     * Intended for testing purposes.
     */
    void deleteAllDeliveries();
}

