package com.neotee.ecommercesystem.shopsystem.storageunit.application.service;

import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.product.application.port.out.ProductAvailabilityPort;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductAvailabilityPortAdapter implements ProductAvailabilityPort {

    private final StorageUnitRepository storageUnitRepository;

    @Override
    public Boolean isInStock(ProductId productId) {
        return storageUnitRepository.findAll().stream()
                .flatMap(storageUnit -> storageUnit.getStockLevels().stream())
                .anyMatch(stockLevel -> stockLevel.getProduct().getId().equals(productId) &&
                        stockLevel.getQuantityInStock() > 0);
    }

    @Override
    public void deleteAllStockLevel() {
        storageUnitRepository.findAll().forEach(storageUnit -> {
            storageUnit.getStockLevels().clear();
            storageUnitRepository.save(storageUnit);
        });
    }
}