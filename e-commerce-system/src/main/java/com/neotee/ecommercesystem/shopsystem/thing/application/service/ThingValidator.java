package com.neotee.ecommercesystem.shopsystem.thing.application.service;

import com.neotee.ecommercesystem.ShopException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ThingValidator {



    public static void validateThingId(UUID thingId) {
        if (thingId == null) {
            throw new ShopException("Thing ID must not be null");
        }
    }

}
