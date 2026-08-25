package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto;

import com.neotee.ecommercesystem.domainprimitives.ShoppingBasketId;
import com.neotee.ecommercesystem.domainprimitives.ClientId;

import java.util.List;

public record ShoppingBasketResponseDTO(
    ShoppingBasketId id,
    ClientId clientId,
    String clientEmail,
    List<ShoppingBasketPartResponseDTO> parts,
    Double totalPrice,
    String basketState,
    int totalQuantity
) {}