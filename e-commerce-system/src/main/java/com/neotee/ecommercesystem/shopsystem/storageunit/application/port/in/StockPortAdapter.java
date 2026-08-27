package com.neotee.ecommercesystem.shopsystem.storageunit.application.port.in;

import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.port.out.StockPort;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.repository.StorageUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockPortAdapter implements StockPort {

    private final StorageUnitRepository storageUnitRepository;

    @Override
    public int getAvailableStock(Product product) {
        if (product == null) {return 0;}

        var storageUnits = storageUnitRepository.findAll();
        var totalStock = 0;

        for (var storageUnit : storageUnits) {
            totalStock += storageUnit.getAvailableStock(product);
        }

        return totalStock;
    }
}