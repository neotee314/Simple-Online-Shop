package com.neotee.ecommercesystem.restdtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateDeliveryPackageStatusRequestDTO(
        @NotBlank(message = "Status is required")
        @Pattern(
                regexp = "NOT_SHIPPED|IN_TRANSIT|DELIVERED",
                message = "Status must be one of: NOT_SHIPPED, IN_TRANSIT, DELIVERED"
        )
        @Schema(description = "Delivery package status", example = "IN_TRANSIT", allowableValues = {"NOT_SHIPPED", "IN_TRANSIT", "DELIVERED"})
        String status
) {}