package com.neotee.ecommercesystem.solution.thing.application;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.MoneyType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ThingValidator {

    public static void validateThingCreation(UUID thingId, String name, String description, Float size,
                                             MoneyType purchasePrice, MoneyType salesPrice) {

        validateThingId(thingId);

        if (isNullOrEmpty(name)) {
            throw new ShopException("Thing name must not be null or empty");
        }

        if (isNullOrEmpty(description)) {
            throw new ShopException("Thing description must not be null or empty");
        }

        if (size != null && size <= 0) {
            throw new ShopException("Thing size must be greater than 0 if provided");
        }

        if (purchasePrice == null || purchasePrice.getAmount() <= 0) {
            throw new ShopException("Purchase price must be greater than zero");
        }

        if (salesPrice == null || salesPrice.getAmount() <= 0) {
            throw new ShopException("Sales price must be greater than zero");
        }
    }

    public static void validateThingId(UUID thingId) {
        if (thingId == null) {
            throw new ShopException("Thing ID must not be null");
        }
    }

    private static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
