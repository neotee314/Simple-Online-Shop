package com.neotee.ecommercesystem.shopsystem.order.application.dto;

import com.neotee.ecommercesystem.domainprimitives.OrderId;

import java.util.List;

public record OrderResponseDTO(
    OrderId orderId,
    String clientEmail,
    List<OrderPartResponseDTO> orderParts,
    String status,
    String submissionDate
) {}