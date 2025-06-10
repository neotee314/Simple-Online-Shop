package com.neotee.ecommercesystem.shopsystem.thing.application.mapper;

import com.neotee.ecommercesystem.shopsystem.thing.application.dto.ResponseThingDTO;
import com.neotee.ecommercesystem.shopsystem.thing.domain.Thing;
import com.neotee.ecommercesystem.shopsystem.thing.domain.ThingId;
import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.MoneyType;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ThingMapper {

    @Mapping(source = "id", target = "thingId", qualifiedByName = "uuidToThingId")
    @Mapping(source = "purchasePrice", target = "purchasePrice", qualifiedByName = "floatToMoney")
    @Mapping(source = "salePrice", target = "salesPrice", qualifiedByName = "floatToMoney")
    Thing toEntity(ResponseThingDTO dto);

    @Mapping(source = "thingId", target = "id", qualifiedByName = "thingIdToUuid")
    @Mapping(source = "purchasePrice", target = "purchasePrice", qualifiedByName = "moneyToFloat")
    @Mapping(source = "salesPrice", target = "salePrice", qualifiedByName = "moneyToFloat")
    ResponseThingDTO toDTO(Thing entity);


    @Named("uuidToThingId")
    static ThingId uuidToThingId(UUID id) {
        return id == null ? null : new ThingId(id);
    }

    @Named("thingIdToUuid")
    static UUID thingIdToUuid(ThingId thingId) {
        return thingId == null ? null : thingId.getId();
    }

    @Named("floatToMoney")
    static Money floatToMoney(Float value) {
        if (value == null) return null;
        MoneyType moneyType =  Money.of(value,"EUR");
        return (Money) moneyType;
    }

    @Named("moneyToFloat")
    static Float moneyToFloat(Money money) {
        return money == null ? null : money.getAmount();
    }
}
