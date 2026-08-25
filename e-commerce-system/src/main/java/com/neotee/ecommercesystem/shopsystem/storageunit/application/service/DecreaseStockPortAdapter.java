package com.neotee.ecommercesystem.shopsystem.storageunit.application.service;

import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.port.out.DecreaseStockPort;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;

@Service
@RequiredArgsConstructor
public class DecreaseStockPortAdapter implements DecreaseStockPort {

    private final StorageUnitRepository storageUnitRepository;

    @Override
    public void decreaseStock(ProductId productId, int quantity) {
        if (productId == null)
            throw new DomainValidationException("DecreaseStockPortAdapter", "Product ID darf nicht null sein.");
        if (quantity <= 0)
            throw new DomainValidationException("DecreaseStockPortAdapter", "Quantity muss größer als 0 sein.");

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
}