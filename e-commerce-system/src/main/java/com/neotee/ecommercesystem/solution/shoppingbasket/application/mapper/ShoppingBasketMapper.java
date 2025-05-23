package com.neotee.ecommercesystem.solution.shoppingbasket.application.mapper;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.solution.shoppingbasket.application.dto.ShoppingBasketDto;
import com.neotee.ecommercesystem.solution.shoppingbasket.domain.BasketState;
import com.neotee.ecommercesystem.solution.shoppingbasket.domain.ShoppingBasket;
import jdk.jfr.Name;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {ShoppingBasketPartMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ShoppingBasketMapper {
    @Mapping(target = "basketState", source = "basketState", qualifiedByName = "mapBasketStateEnumToString")
    @Mapping(target = "clientEmail", source = "clientEmail", qualifiedByName = "mapEmailToString")
    public abstract ShoppingBasketDto toDto(ShoppingBasket shoppingBasket);


    @Mapping(target = "basketState", source = "basketState", qualifiedByName = "mapEnumBasketStateToString")
    @Mapping(target = "clientEmail", source = "clientEmail", qualifiedByName = "mapStringToEmail")
    public abstract ShoppingBasket toEntity(ShoppingBasketDto shoppingBasketDto);

    @Name("mapBasketStateEnumToString")
    public String mapBasketStateEnumToString(BasketState basketState) {
        return basketState.toString();
    }

    @Name("mapEnumBasketStateToString")
    public String mapEnumBasketStateToString(String basketState) {
        return BasketState.valueOf(basketState).toString();
    }

    @Name("mapEmailToString")
    public String mapEmailToString(Email email) {
        return email.getEmailAddress();
    }

    @Name("mapStringToEmail")
    public Email mapStringToEmail(String email) {
        return Email.of(email);
    }

}
