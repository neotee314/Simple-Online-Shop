package com.neotee.ecommercesystem.solution.shoppingbasket.application;

import com.neotee.ecommercesystem.solution.shoppingbasket.domain.ShoppingBasketPart;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingBasketPartDto {
    private UUID thingId ;
    private int quantity = 0;


    public ShoppingBasketPart toShoppingBasketPart(ShoppingBasketPartDto dto) {
        ShoppingBasketPart part = new ShoppingBasketPart(dto.getThingId(),dto.getQuantity());

        return part;
    }

    public ShoppingBasketPartDto shoppingBasketPartDto(ShoppingBasketPart part) {
        ShoppingBasketPartDto dto = new ShoppingBasketPartDto();
        dto.setQuantity(part.getQuantity());
        dto.setThingId(part.getThingId());
        return dto;

    }
}
