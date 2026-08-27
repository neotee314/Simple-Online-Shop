package com.neotee.ecommercesystem.exceptions;

public class EntityNotFoundException extends ShopException {

    private final String field;

    public EntityNotFoundException(String field, String message) {
        super(field + ": " + message);
        this.field = field;
    }

    public String field() {
        return field;
    }
}