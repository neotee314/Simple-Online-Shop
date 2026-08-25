package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto;

import com.neotee.ecommercesystem.domainprimitives.ProductId;

public record ShoppingBasketPartResponseDTO(
    ProductId productId,
    String productName,
    int quantity,
    Double salesPrice,
    Double subtotal
) {}