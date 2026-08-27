package com.neotee.ecommercesystem.shopsystem.storageunit.application.port.in;

import com.neotee.ecommercesystem.shopsystem.product.application.port.out.ProductAvailabilityPort;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.repository.StorageUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductAvailabilityPortAdapter implements ProductAvailabilityPort {

    private final StorageUnitRepository storageUnitRepository;

    @Override
    public Boolean isInStock(Product product) {
        return storageUnitRepository.findAll().stream()
                .flatMap(storageUnit -> storageUnit.getStockLevels().stream())
                .anyMatch(stockLevel -> stockLevel.getProduct().equals(product) &&
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