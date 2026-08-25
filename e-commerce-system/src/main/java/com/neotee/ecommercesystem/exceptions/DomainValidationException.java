package com.neotee.ecommercesystem.exceptions;

//422
public class DomainValidationException extends ShopException {
    private final String field;

    public DomainValidationException(String field, String message) {
        super(field + ": " + message);
        this.field = field;
    }

    public String field() {
        return field;
    }
}