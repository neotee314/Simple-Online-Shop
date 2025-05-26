package com.neotee.ecommercesystem.solution.shoppingbasket.application.mapper;

import com.neotee.ecommercesystem.solution.shoppingbasket.application.dto.ShoppingBasketPartDto;
import com.neotee.ecommercesystem.solution.shoppingbasket.domain.ShoppingBasketPart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShoppingBasketPartMapper {
    @Mapping(target = "thingId", source = "thingId")
    @Mapping(target = "quantity", source = "quantity")
    ShoppingBasketPartDto toDto(ShoppingBasketPart shoppingBasketPart);


    @Mapping(target = "thingId", source = "thingId")
    @Mapping(target = "quantity", source = "quantity")
    ShoppingBasketPart toEntity(ShoppingBasketPartDto shoppingBasketPartDto);
}
