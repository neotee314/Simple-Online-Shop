package com.neotee.ecommercesystem.shopsystem.storageunit.application.dto;

import com.neotee.ecommercesystem.domainprimitives.StorageUnitId;

import java.util.List;

public record StorageUnitResponseDTO(
        StorageUnitId id,
        String name,
        String street,
        String city,
        String zipCode,
        List<StockLevelResponseDTO> stockLevels
) {}