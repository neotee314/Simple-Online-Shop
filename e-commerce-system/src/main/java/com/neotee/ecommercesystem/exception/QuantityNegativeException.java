package com.neotee.ecommercesystem.exception;
import com.neotee.ecommercesystem.ShopException;

public class QuantityNegativeException extends ShopException {
    public QuantityNegativeException() {
        super("Quantity is negative");
    }
}
