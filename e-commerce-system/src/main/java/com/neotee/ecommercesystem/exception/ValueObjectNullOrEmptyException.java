package com.neotee.ecommercesystem.exception;
import com.neotee.ecommercesystem.ShopException;

public class ValueObjectNullOrEmptyException extends ShopException {
    public ValueObjectNullOrEmptyException() {
        super("value object is null or empty");
    }
}
