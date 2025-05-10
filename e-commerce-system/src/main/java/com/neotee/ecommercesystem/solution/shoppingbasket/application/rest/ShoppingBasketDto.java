package com.neotee.ecommercesystem.solution.shoppingbasket.application.rest;

import com.neotee.ecommercesystem.solution.shoppingbasket.domain.ShoppingBasket;
import com.neotee.ecommercesystem.solution.shoppingbasket.domain.ShoppingBasketPart;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ShoppingBasketDto {

    private UUID id ;
    private String totalSalesPrice ;
    private List<ShoppingBasketPartDto> shoppingBasketParts ;


    public ShoppingBasket toShoppingBaskt(ShoppingBasketDto dto) {
        ShoppingBasket basket = new ShoppingBasket();
        basket.setId(dto.getId());
        List<ShoppingBasketPart> partList = new ArrayList<>();
        for (ShoppingBasketPartDto partDto : dto.getShoppingBasketParts()) {
            //partList.add(mapper.toShoppingBasketPart(partDto));
        }
        basket.setParts(partList);
        return basket;

    }

    public ShoppingBasketDto toShoppingBasketDto(ShoppingBasket shoppingBasket) {

        ShoppingBasketDto dto = new ShoppingBasketDto();
        dto.setId(shoppingBasket.getId());
        List<ShoppingBasketPartDto> partDtoList = new ArrayList<>();
        for (ShoppingBasketPart part : shoppingBasket.getParts()) {
            //partDtoList.add(mapper.shoppingBasketPartDto(part));
        }
        dto.setShoppingBasketParts(partDtoList);
        return dto;
    }

}
