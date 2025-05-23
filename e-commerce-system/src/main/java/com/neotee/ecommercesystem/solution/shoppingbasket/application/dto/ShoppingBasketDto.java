package com.neotee.ecommercesystem.solution.shoppingbasket.application.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ShoppingBasketDto {
    private UUID id;
    private String totalSalesPrice;
    private String clientEmail;
    private String basketState;
    private List<ShoppingBasketPartDto> shoppingBasketParts;
}
