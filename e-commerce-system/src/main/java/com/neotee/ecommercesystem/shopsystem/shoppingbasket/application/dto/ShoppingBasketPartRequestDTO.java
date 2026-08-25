package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto;

import com.neotee.ecommercesystem.domainprimitives.ProductId;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ShoppingBasketPartRequestDTO(
    @NotNull(message = "Product ID darf nicht null sein")
    ProductId productId,
    
    @Positive(message = "Quantity muss größer als 0 sein")
    int quantity
) {}