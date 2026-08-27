package com.neotee.ecommercesystem.shopsystem.storageunit.application.mapper;

import com.neotee.ecommercesystem.shopsystem.storageunit.application.dto.StockLevelResponseDTO;
import com.neotee.ecommercesystem.shopsystem.storageunit.application.dto.StorageUnitResponseDTO;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.model.StockLevel;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.model.StorageUnit;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class StorageUnitMapper {

    public StorageUnitResponseDTO toResponseDTO(StorageUnit storageUnit) {
        if (storageUnit == null) {
            return null;
        }

        var stockLevels = storageUnit.getStockLevels().stream()
                .map(this::toStockLevelResponseDTO)
                .collect(Collectors.toList());

        return new StorageUnitResponseDTO(
                storageUnit.getId(),
                storageUnit.getName(),
                storageUnit.getAddress() != null ? storageUnit.getAddress().getStreet() : null,
                storageUnit.getAddress() != null ? storageUnit.getAddress().getCity() : null,
                storageUnit.getAddress() != null && storageUnit.getAddress().getZipCode() != null
                        ? storageUnit.getAddress().getZipCode().getZipCode()
                        : null,
                stockLevels
        );
    }

    public StockLevelResponseDTO toStockLevelResponseDTO(StockLevel stockLevel) {
        if (stockLevel == null) {
            return null;
        }

        return new StockLevelResponseDTO(
                stockLevel.getProduct() != null ? stockLevel.getProduct().getId() : null,
                stockLevel.getProduct() != null ? stockLevel.getProduct().getName() : null,
                stockLevel.getQuantityInStock()
        );
    }
}