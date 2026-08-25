package com.neotee.ecommercesystem.shopsystem.product.application.service;

import com.neotee.ecommercesystem.exception.ShopException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProductValidator {



    public static void validateThingId(UUID thingId) {
        if (thingId == null) {
            throw new ShopException("Thing ID must not be null");
        }
    }

}
