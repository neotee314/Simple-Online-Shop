package com.neotee.ecommercesystem.exception;


import com.neotee.ecommercesystem.ShopException;

public class ThingNotInShoppingBasketException extends ShopException {
    public ThingNotInShoppingBasketException() {
        super("Thing is not in the shopping basket");
    }
}
