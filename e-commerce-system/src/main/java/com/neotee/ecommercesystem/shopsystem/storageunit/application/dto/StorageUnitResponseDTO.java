package com.neotee.ecommercesystem.shopsystem.storageunit.application.dto;

import com.neotee.ecommercesystem.domainprimitives.HomeAddress;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorageUnitResponseDTO {
    private UUID storageId;
    private String name;
    private HomeAddress address;
    private List<StockLevelDTO> stockLevels;
}

