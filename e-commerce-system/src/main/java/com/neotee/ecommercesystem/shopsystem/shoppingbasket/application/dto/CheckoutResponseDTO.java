package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto;

import java.util.UUID;

public record CheckoutResponseDTO(
    UUID orderId
) {}