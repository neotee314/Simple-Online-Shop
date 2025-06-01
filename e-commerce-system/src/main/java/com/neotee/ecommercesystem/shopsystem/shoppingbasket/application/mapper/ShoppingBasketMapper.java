package com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.mapper;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto.ShoppingBasketDTO;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.BasketState;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.ShoppingBasket;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.ShoppingBasketId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.UUID;

@Mapper(componentModel = "spring", uses = {ShoppingBasketPartMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ShoppingBasketMapper {
    @Mapping(target = "id", source = "id", qualifiedByName = "mapBasketIdToUUID")
    @Mapping(target = "basketState", source = "basketState", qualifiedByName = "mapBasketStateEnumToString")
    @Mapping(target = "clientEmail", source = "clientEmail", qualifiedByName = "mapEmailToString")
    @Mapping(target = "totalSalesPrice", source = ".", qualifiedByName = "getTotalSalesPrice")
    @Mapping(target = "shoppingBasketParts", source = "parts")
    public abstract ShoppingBasketDTO toDto(ShoppingBasket shoppingBasket);

    @Mapping(target = "id", source = "id", qualifiedByName = "mapUUIDToBasketId")
    @Mapping(target = "basketState", source = "basketState", qualifiedByName = "mapEnumBasketStateToString")
    @Mapping(target = "clientEmail", source = "clientEmail", qualifiedByName = "mapStringToEmail")
    @Mapping(target = "parts", source = "shoppingBasketParts")
    public abstract ShoppingBasket toEntity(ShoppingBasketDTO shoppingBasketDto);

    @Named("mapUUIDToBasketId")
    public ShoppingBasketId mapUUIDToBasketId(UUID id) {
        return new ShoppingBasketId(id);
    }

    @Named("mapBasketIdToUUID")
    public UUID mapBasketIdToUUID(ShoppingBasketId shoppingBasketId) {
        return shoppingBasketId.getId();
    }

    @Named("getTotalSalesPrice")
    public String getTotalSalesPrice(ShoppingBasket shoppingBasket) {
        DecimalFormat df = new DecimalFormat("0.00");
        df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.GERMANY));

        String formatted = df.format(shoppingBasket.getTotalSalesPrice().getAmount());
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
