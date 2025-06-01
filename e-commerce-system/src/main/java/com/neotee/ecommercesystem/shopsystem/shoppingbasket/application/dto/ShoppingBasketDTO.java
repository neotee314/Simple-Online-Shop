package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShoppingBasketDTO {
    private UUID id;
    private String totalSalesPrice;
    private String clientEmail;
    private String basketState;
    private List<ShoppingBasketPartDto> shoppingBasketParts;
}
