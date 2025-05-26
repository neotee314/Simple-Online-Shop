package com.neotee.ecommercesystem.solution.shoppingbasket.application.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingBasketPartDto {
    private UUID thingId ;
    private Integer quantity;

}
