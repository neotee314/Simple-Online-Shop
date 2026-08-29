package com.neotee.ecommercesystem.shopsystem.delivery.application.dto;

import java.util.UUID;

public record DeliveryPackageResponseDTO(
        UUID id,
        String status
) {}