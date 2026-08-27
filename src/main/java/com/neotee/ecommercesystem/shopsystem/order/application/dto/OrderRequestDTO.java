package com.neotee.ecommercesystem.shopsystem.order.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderRequestDTO(
    @NotBlank(message = "Client Email darf nicht leer sein")
    @Email(message = "Ungültiges E-Mail-Format")
    String clientEmail,
    
    @NotNull(message = "Order Parts darf nicht null sein")
    OrderPartRequestDTO orderPart
) {}