package com.neotee.ecommercesystem.restdtos;

import java.util.UUID;

public record ShoppingBasketPartResponseDTO(UUID productId, String productName, int quantity, Double salesPrice, Double subtotal) {}
