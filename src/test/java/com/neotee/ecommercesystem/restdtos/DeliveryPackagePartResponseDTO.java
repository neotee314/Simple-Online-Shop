package com.neotee.ecommercesystem.restdtos;

import java.util.UUID;

public record DeliveryPackagePartResponseDTO(UUID productId, String productName, int quantity) {}