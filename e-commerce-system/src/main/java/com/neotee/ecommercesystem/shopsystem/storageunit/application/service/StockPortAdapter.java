package com.neotee.ecommercesystem.shopsystem.storageunit.application.service;

import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.port.out.StockPort;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockPortAdapter implements StockPort {

    private final StorageUnitRepository storageUnitRepository;

    @Override
    public void decreaseStock(ProductId productId, int quantity) {
        if (productId == null) {
            throw new DomainValidationException("DecreaseStockPortAdapter", "Product ID darf nicht null sein.");
        }
        if (quantity <= 0) {
            throw new DomainValidationException("DecreaseStockPortAdapter", "Quantity muss größer als 0 sein.");
        }

        var storageUnits = storageUnitRepository.findAll();
        var remainingQuantity = quantity;

        for (var storageUnit : storageUnits) {
            if (remainingQuantity <= 0) break;

            var available = storageUnit.getAvailableStock(productId);
            if (available > 0) {
                var toRemove = Math.min(available, remainingQuantity);
                storageUnit.removeFromStock(productId, toRemove);
                storageUnitRepository.save(storageUnit);
                remainingQuantity -= toRemove;
            }
        }

        if (remainingQuantity > 0) {
            throw new DomainValidationException("DecreaseStockPortAdapter",
                    "Nicht genügend Lagerbestand vorhanden. " + remainingQuantity + " Stück fehlen.");
        }
    }

    @Override
    public boolean hasEnoughStock(ProductId productId, int quantity) {
        if (productId == null || quantity <= 0) {
            return false;
        }
        return getAvailableStock(productId) >= quantity;
    }

    @Override
    public int getAvailableStock(ProductId productId) {
        if (productId == null) {
            return 0;
        }

        var storageUnits = storageUnitRepository.findAll();
        var totalStock = 0;

        for (var storageUnit : storageUnits) {
            totalStock += storageUnit.getAvailableStock(productId);
        }

        return totalStock;
    }
}