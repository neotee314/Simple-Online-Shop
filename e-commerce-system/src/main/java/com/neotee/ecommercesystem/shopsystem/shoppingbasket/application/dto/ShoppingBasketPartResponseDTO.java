package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto;

import java.util.UUID;

public record ShoppingBasketPartResponseDTO(
    UUID productId,
    String productName,
    Integer quantity,
    Double salesPrice
) {}