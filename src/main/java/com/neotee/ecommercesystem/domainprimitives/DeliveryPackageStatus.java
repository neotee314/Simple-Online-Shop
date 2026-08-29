package com.neotee.ecommercesystem.domainprimitives;


/**
 * Represents the current status of an individual delivery package.
 */
public enum DeliveryPackageStatus {
    /**
     * The package has not been shipped yet.
     */
    NOT_SHIPPED,
    /**
     * The package has been shipped but has not arrived yet.
     */
    IN_TRANSIT,
    /**
     * The package has been delivered to the recipient.
     */
    DELIVERED
}