package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.mapper;

import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto.ShoppingBasketPartResponseDTO;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto.ShoppingBasketResponseDTO;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.ShoppingBasket;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.ShoppingBasketPart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ShoppingBasketMapper {

    public ShoppingBasketResponseDTO toResponseDTO(ShoppingBasket basket) {
        if (basket == null) return null;

        var partDTOs = basket.getParts().stream()
                .map(this::toPartResponseDTO)
                .collect(Collectors.toList());

        return new ShoppingBasketResponseDTO(
                basket.getId().getId(),
                basket.getClient() != null ? basket.getClient().getId().getId() : null,
                basket.getClient() != null ? basket.getClient().getEmail().getEmailAddress() : null,
                partDTOs,
                basket.getTotalPrice() != null ? basket.getTotalPrice().getAmount().doubleValue() : 0.0,
                basket.getBasketState() != null ? basket.getBasketState().name() : "EMPTY",
                basket.getTotalQuantity()
        );
    }

    public ShoppingBasketPartResponseDTO toPartResponseDTO(ShoppingBasketPart part) {
        if (part == null) return null;

        return new ShoppingBasketPartResponseDTO(
                part.getProduct().getId().getId(),
                part.getProduct().getName(),
                part.getQuantity(),
                part.getSalesPrice() != null ? part.getSalesPrice().getAmount().doubleValue() : 0.0);
    }

}