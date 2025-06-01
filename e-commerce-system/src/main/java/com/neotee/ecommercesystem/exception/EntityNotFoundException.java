package com.neotee.ecommercesystem.exception;
import com.neotee.ecommercesystem.ShopException;

public class EntityNotFoundException extends ShopException {
    public EntityNotFoundException() {
        super("Entity not found");
    }
}
