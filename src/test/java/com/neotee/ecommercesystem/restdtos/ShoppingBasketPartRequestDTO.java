package com.neotee.ecommercesystem.restdtos;

import java.util.UUID;

public record ShoppingBasketPartRequestDTO(UUID productId, int quantity) {}