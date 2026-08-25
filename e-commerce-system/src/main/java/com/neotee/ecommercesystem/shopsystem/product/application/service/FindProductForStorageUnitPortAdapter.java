package com.neotee.ecommercesystem.shopsystem.product.application.service;

import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.product.domain.ProductRepository;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.port.out.FindProductForShoppingBasketPort;
import com.neotee.ecommercesystem.shopsystem.storageunit.application.port.out.FindProductForStorageUnitPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FindProductForStorageUnitPortAdapter implements FindProductForStorageUnitPort, FindProductForShoppingBasketPort {

    private final ProductRepository productRepository;

    @Override
    public Product findById(ProductId productId) {
        if (productId == null)
            throw new DomainValidationException("FindProductPortAdapter", "Product ID darf nicht null sein.");

        return productRepository.findById(productId)
                .orElseThrow(() -> new DomainValidationException("FindProductPortAdapter", "Produkt nicht gefunden."));
    }
}