package com.neotee.ecommercesystem.exceptions;

//409
public class ItemNotInBasketException extends ShopException {
    String field;

    public ItemNotInBasketException(String field, String message) {
        super(field + ": " + message);
        this.field = field;
    }

    public String field() {
        return field;
    }
}