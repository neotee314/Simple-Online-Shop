package com.neotee.ecommercesystem.shopsystem.client.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClientRequestDto(
        @NotBlank(message = "Name darf nicht leer sein")
        String name,

        @NotBlank(message = "E-Mail darf nicht leer sein")
        @Email(message = "Ungültiges E-Mail-Format")
        String email,

        @NotBlank(message = "Straße darf nicht leer sein")
        String street,

        @NotBlank(message = "Stadt darf nicht leer sein")
        String city,

        @NotBlank(message = "Postleitzahl darf nicht leer sein")
        String zipCode
) {}