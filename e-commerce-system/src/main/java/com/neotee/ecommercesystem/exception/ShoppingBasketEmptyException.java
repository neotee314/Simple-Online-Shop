package com.neotee.ecommercesystem.exception;

import com.neotee.ecommercesystem.ShopException;

public class ShoppingBasketEmptyException extends ShopException {
    public ShoppingBasketEmptyException() {
        super("Shopping basket is empty");
    }
}