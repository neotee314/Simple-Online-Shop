package com.neotee.ecommercesystem.shopsystem.storageunit.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StorageUnitRequestDTO(
        @NotBlank(message = "Name darf nicht leer sein")
        String name,

        @NotBlank(message = "Straße darf nicht leer sein")
        String street,

        @NotBlank(message = "Stadt darf nicht leer sein")
        String city,

        @NotBlank(message = "Postleitzahl darf nicht leer sein")
        String zipCode
) {}