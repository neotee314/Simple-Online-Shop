package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto;

import com.neotee.ecommercesystem.domainprimitives.OrderId;

public record CheckoutResponseDTO(
    OrderId orderId
) {}