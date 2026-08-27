package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record ShoppingBasketPartRequestDTO(
    @NotNull(message = "Product ID darf nicht null sein")
    UUID productId,
    
    @Positive(message = "Quantity muss größer als 0 sein")
    Integer quantity
) {}