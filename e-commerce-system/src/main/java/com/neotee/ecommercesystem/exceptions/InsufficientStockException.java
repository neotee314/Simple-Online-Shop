package com.neotee.ecommercesystem.exceptions;

public class InsufficientStockException extends ShopException {

    private final String field;

    public InsufficientStockException(String field, String message) {
        super(field + ": " + message);
        this.field = field;
    }

    public String field() {
        return field;
    }
}