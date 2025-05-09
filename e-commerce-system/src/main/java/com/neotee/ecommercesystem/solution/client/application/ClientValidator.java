package com.neotee.ecommercesystem.solution.client.application;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.HomeAddressType;
import org.springframework.stereotype.Component;

@Component

public class ClientValidator {

    private ClientValidator() {
        // private constructor to prevent instantiation
    }

    public static void validateCreateClient(String name, EmailType email, HomeAddressType address) {
        if (name == null || name.trim().isEmpty()) {
            throw new ShopException("Name cannot be empty");
        }
        if (email == null) {
            throw new ShopException("Email cannot be null");
        }
        validateAddress(address);
    }

    public static void validateUpdateAddress(EmailType email, HomeAddressType address) {
        if (email == null) {
            throw new ShopException("Email cannot be null");
        }
        validateAddress(address);
    }

    private static void validateAddress(HomeAddressType address) {
        if (address == null) {
            throw new ShopException("Address cannot be null");
        }
        if (address.getStreet() == null || address.getStreet().trim().isEmpty()) {
            throw new ShopException("Street cannot be empty");
        }
        if (address.getCity() == null || address.getCity().trim().isEmpty()) {
            throw new ShopException("City cannot be empty");
        }
        if (address.getZipCode() == null) {
            throw new ShopException("ZipCode cannot be null");
        }
    }
}
