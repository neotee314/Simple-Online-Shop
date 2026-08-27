package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto;


import java.util.List;
import java.util.UUID;

public record ShoppingBasketResponseDTO(
    UUID id,
    UUID clientId,
    String clientEmail,
    List<ShoppingBasketPartResponseDTO> parts,
    Double totalPrice,
    String basketState,
    Integer totalQuantity
) {}