package com.neotee.ecommercesystem.shopsystem.deliverypackage.application.mapper;

import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.dto.DeliveryPackagePartDTO;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.domain.DeliveryPackagePart;
import com.neotee.ecommercesystem.shopsystem.thing.application.service.ThingService;
import com.neotee.ecommercesystem.shopsystem.thing.domain.Thing;
import org.mapstruct.*;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class DeliveryPackagePartMapper {

    @Autowired
    protected ThingService thingService;

    @Mapping(target = "thingId", source = "thing", qualifiedByName = "mapThingToUUID")
    public abstract DeliveryPackagePartDTO toDto(DeliveryPackagePart part);

    @Mapping(target = "thing", source = "thingId", qualifiedByName = "mapUUIDToThing")
    public abstract DeliveryPackagePart toEntity(DeliveryPackagePartDTO partDto);

    @Named("mapThingToUUID")
    public UUID mapThingToUUID(Thing thing) {
        return thing.getThingId().getId();
    }

    @Named("mapUUIDToThing")
    public Thing mapUUIDToThing(UUID id) {
        return thingService.findById(id);
    }
}
