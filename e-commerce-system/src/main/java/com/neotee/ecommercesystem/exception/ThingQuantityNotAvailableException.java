package com.neotee.ecommercesystem.exception;

import com.neotee.ecommercesystem.ShopException;

// ThingQuantityNotAvailableException.java
public class ThingQuantityNotAvailableException extends ShopException {
    public ThingQuantityNotAvailableException() {
        super("Thing is not available in the requested quantity");
    }
}
