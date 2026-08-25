package com.neotee.ecommercesystem.shopsystem.product.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SalesPriceDTO(
    @NotNull(message = "Verkaufspreis darf nicht null sein")
    @Positive(message = "Verkaufspreis muss größer als 0 sein")
    Float salesPrice,
    
    @NotBlank(message = "Währung darf nicht leer sein")
    String currency
) {}