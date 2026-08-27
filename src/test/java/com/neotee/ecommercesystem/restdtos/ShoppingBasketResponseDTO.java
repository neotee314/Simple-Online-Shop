package com.neotee.ecommercesystem.restdtos;

import java.util.List;
import java.util.UUID;

public record ShoppingBasketResponseDTO(UUID id, UUID clientId, String clientEmail, List<ShoppingBasketPartResponseDTO> parts, Double totalPrice, String basketState, int totalQuantity) {}
