package com.neotee.ecommercesystem.solution.shoppingbasket.application.mapper;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.solution.shoppingbasket.application.dto.ShoppingBasketDto;
import com.neotee.ecommercesystem.solution.shoppingbasket.domain.BasketState;
import com.neotee.ecommercesystem.solution.shoppingbasket.domain.ShoppingBasket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.text.NumberFormat;
import java.util.Locale;

@Mapper(componentModel = "spring", uses = {ShoppingBasketPartMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ShoppingBasketMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "basketState", source = "basketState", qualifiedByName = "mapBasketStateEnumToString")
    @Mapping(target = "clientEmail", source = "clientEmail", qualifiedByName = "mapEmailToString")
    @Mapping(target = "totalSalesPrice", source = ".", qualifiedByName = "getTotalSalesPrice")
    @Mapping(target = "shoppingBasketParts", source = "parts")
    public abstract ShoppingBasketDto toDto(ShoppingBasket shoppingBasket);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "basketState", source = "basketState", qualifiedByName = "mapEnumBasketStateToString")
    @Mapping(target = "clientEmail", source = "clientEmail", qualifiedByName = "mapStringToEmail")
    @Mapping(target = "parts", source = "shoppingBasketParts")
    public abstract ShoppingBasket toEntity(ShoppingBasketDto shoppingBasketDto);


    @Named("getTotalSalesPrice")
    public String getTotalSalesPrice(ShoppingBasket shoppingBasket) {
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.GERMANY);
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);

        String formatted = currencyFormat.format(shoppingBasket.getTotalSalesPrice().getAmount());
        return formatted + " €";
    }


    @Named("mapBasketStateEnumToString")
    public String mapBasketStateEnumToString(BasketState basketState) {
        return basketState.toString();
    }

    @Named("mapEnumBasketStateToString")
    public String mapEnumBasketStateToString(String basketState) {
        return BasketState.valueOf(basketState).toString();
    }

    @Named("mapEmailToString")
    public String mapEmailToString(Email email) {
        return email.getEmailAddress();
    }

    @Named("mapStringToEmail")
    public Email mapStringToEmail(String email) {
        return Email.of(email);
    }

}
