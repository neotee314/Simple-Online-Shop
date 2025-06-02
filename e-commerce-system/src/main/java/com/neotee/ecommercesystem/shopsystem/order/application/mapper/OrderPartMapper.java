package com.neotee.ecommercesystem.shopsystem.order.application.mapper;

import com.neotee.ecommercesystem.shopsystem.order.application.dto.OrderPartDTO;
import com.neotee.ecommercesystem.shopsystem.order.domain.OrderPart;
import com.neotee.ecommercesystem.shopsystem.thing.application.service.ThingService;
import com.neotee.ecommercesystem.shopsystem.thing.domain.Thing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class OrderPartMapper {

    @Autowired
    protected ThingService thingService;

    @Mapping(target = "thingId", source = "thing", qualifiedByName = "mapThingToUUID")
    @Mapping(target = "quantity", source = "orderQuantity")
    public abstract OrderPartDTO toDto(OrderPart orderPart);

    @Mapping(target = "thing", source = "thingId", qualifiedByName = "mapUUIDToThing")
    @Mapping(target = "orderQuantity", source = "quantity")
    public abstract OrderPart toEntity(OrderPartDTO orderPartDTO);

    @Named("mapThingToUUID")
    public UUID mapThingToUUID(Thing thing) {
        return thing.getThingId().getId();
    }

    @Named("mapUUIDToThing")
    public Thing mapUUIDToThing(UUID thingId) {
        return thingService.findById(thingId);
    }
}
