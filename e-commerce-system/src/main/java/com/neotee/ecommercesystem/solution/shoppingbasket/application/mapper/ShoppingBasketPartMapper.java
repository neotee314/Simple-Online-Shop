package com.neotee.ecommercesystem.solution.shoppingbasket.application.mapper;

import com.neotee.ecommercesystem.solution.shoppingbasket.application.dto.ShoppingBasketPartDto;
import com.neotee.ecommercesystem.solution.shoppingbasket.domain.ShoppingBasketPart;
import com.neotee.ecommercesystem.solution.shoppingbasket.domain.ShoppingBasketPartId;
import com.neotee.ecommercesystem.solution.thing.application.service.ThingService;
import com.neotee.ecommercesystem.solution.thing.domain.Thing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ShoppingBasketPartMapper {

    @Autowired
    private ThingService thingService;

    @Mapping(target = "thingId", source = "thing", qualifiedByName = "mapThingToUUID")
    @Mapping(target = "quantity", source = "quantity")
    public abstract ShoppingBasketPartDto toDto(ShoppingBasketPart shoppingBasketPart);


    @Mapping(target = "thing", source = "thingId", qualifiedByName = "mapUUIDToThing")
    @Mapping(target = "quantity", source = "quantity")
    public abstract ShoppingBasketPart toEntity(ShoppingBasketPartDto shoppingBasketPartDto);

    @Named("mapUUIDToShoppingBasketPartId")
    public ShoppingBasketPartId mapUUIDToShoppingBasketPartId(UUID id) {
        return new ShoppingBasketPartId(id);
    }

    @Named("mapShoppingBasketPartIdToUUID")
    public UUID mapShoppingBasketPartIdToUUID(ShoppingBasketPartId shoppingBasketPartId) {
        return shoppingBasketPartId.getId();
    }

    @Named("mapThingToUUID")
    public UUID mapThingToUUID(Thing thing) {
        return thing.getThingId().getId();
    }
    @Named("mapUUIDToThing")
    public Thing mapUUIDToThing(UUID thingId) {
        Thing thing = thingService.findById(thingId);
        if (thing == null) return null;
        return thing;
    }

}
