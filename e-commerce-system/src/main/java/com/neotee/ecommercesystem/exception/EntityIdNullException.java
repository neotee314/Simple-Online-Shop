package com.neotee.ecommercesystem.exception;


import com.neotee.ecommercesystem.ShopException;

public class EntityIdNullException extends ShopException {
    public EntityIdNullException() {
        super("ThingId is null");
    }
}
