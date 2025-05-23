package com.neotee.ecommercesystem.solution.shoppingbasket.application.mapper;

import com.neotee.ecommercesystem.solution.shoppingbasket.application.dto.ShoppingBasketPartDto;
import com.neotee.ecommercesystem.solution.shoppingbasket.domain.ShoppingBasketPart;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShoppingBasketPartMapper {
    ShoppingBasketPartDto toDto(ShoppingBasketPart shoppingBasketPart);

    ShoppingBasketPart toEntity(ShoppingBasketPartDto shoppingBasketPartDto);
}
