package com.neotee.ecommercesystem.shopsystem.storageunit.application.mapper;

import com.neotee.ecommercesystem.shopsystem.storageunit.application.dto.StorageUnitDTO;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnit;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnitId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {StockLevelMapper.class})
public abstract class StorageUnitMapper {

    @Mapping(target = "storageId", source = "storageId", qualifiedByName = "mapStorageUnitIdToUUID")
    public abstract StorageUnitDTO toDto(StorageUnit storageUnit);

    @Mapping(target = "storageId", source = "storageId", qualifiedByName = "mapUUIDToStorageUnitId")
    public abstract StorageUnit toEntity(StorageUnitDTO storageUnitDTO);

    @Named("mapStorageUnitIdToUUID")
    public UUID mapStorageUnitIdToUUID(StorageUnitId storageUnitId) {
        return storageUnitId.getId();
    }

    @Named("mapUUIDToStorageUnitId")
    public StorageUnitId mapUUIDToStorageUnitId(UUID uuid) {
        return new StorageUnitId(uuid);
    }
}
