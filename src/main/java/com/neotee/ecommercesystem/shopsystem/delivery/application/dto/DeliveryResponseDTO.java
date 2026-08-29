package com.neotee.ecommercesystem.shopsystem.delivery.application.dto;

import java.util.List;
import java.util.UUID;

public record DeliveryResponseDTO(
        UUID id,
        UUID orderId,
        String clientName,
        String clientEmail,
        List<UUID> packageIds
) {}