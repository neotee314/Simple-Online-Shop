package com.neotee.ecommercesystem.shopsystem.storageunit.application.mapper;

import com.neotee.ecommercesystem.shopsystem.storageunit.application.dto.StockLevelDTO;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StockLevel;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StockLevelId;
import com.neotee.ecommercesystem.shopsystem.thing.application.service.ThingService;
import com.neotee.ecommercesystem.shopsystem.thing.domain.Thing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class StockLevelMapper {

    @Autowired
    protected ThingService thingService;

    @Mapping(target = "stockLevelId", source = "stockLevelId", qualifiedByName = "mapStockLevelIdToUUID")
    @Mapping(target = "thingId", source = "thing", qualifiedByName = "mapThingToUUID")
    @Mapping(target = "quantityInStock", source = "quantityInStock")
    public abstract StockLevelDTO toDto(StockLevel stockLevel);

    @Mapping(target = "stockLevelId", source = "stockLevelId", qualifiedByName = "mapUUIDToStockLevelId")
    @Mapping(target = "thing", source = "thingId", qualifiedByName = "mapUUIDToThing")
    @Mapping(target = "quantityInStock", source = "quantityInStock")
    public abstract StockLevel toEntity(StockLevelDTO stockLevelDto);

    @Named("mapUUIDToStockLevelId")
    public StockLevelId mapUUIDToStockLevelId(UUID id) {
        return new StockLevelId(id);
    }

    @Named("mapStockLevelIdToUUID")
    public UUID mapStockLevelIdToUUID(StockLevelId stockLevelId) {
        return stockLevelId.getId();
    }

    @Named("mapThingToUUID")
    public UUID mapThingToUUID(Thing thing) {
        return thing.getThingId().getId();
    }

    @Named("mapUUIDToThing")
    public Thing mapUUIDToThing(UUID thingId) {
        return thingService.findById(thingId);
    }
}
