package com.neotee.ecommercesystem.anticorruption;

import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.product.application.service.ProductApplicationService;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.usecases.ProductCatalogUseCases;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.MoneyType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MyFantasticProductCatalogService implements ProductCatalogUseCases {

    private final ProductApplicationService productApplicationService;

    @Override
    @Transactional
    public void addProductToCatalog(UUID thingId, String name, String description, Float size,
                                    MoneyType purchasePrice, MoneyType salesPrice) {
        if (thingId == null)
            throw new DomainValidationException("MyFantasticProductCatalogService", "Thing ID darf nicht null sein.");
        if (name == null || name.isBlank())
            throw new DomainValidationException("MyFantasticProductCatalogService", "Name darf nicht leer sein.");
        if (description == null || description.isBlank())
            throw new DomainValidationException("MyFantasticProductCatalogService", "Beschreibung darf nicht leer sein.");
        if (purchasePrice == null)
            throw new DomainValidationException("MyFantasticProductCatalogService", "Purchase Price darf nicht null sein.");
        if (salesPrice == null)
            throw new DomainValidationException("MyFantasticProductCatalogService", "Sales Price darf nicht null sein.");

        var product = Product.create(
                ProductId.of(thingId),
                name,
                description,
                size,
                (Money) purchasePrice,
                (Money) salesPrice
        );
        productApplicationService.addProduct(product);
    }

    @Override
    public void removeProductFromCatalog(UUID productUuid) {
        if (productUuid == null)
            throw new DomainValidationException("MyFantasticProductCatalogService", "Product UUID darf nicht null sein.");

        var productId = ProductId.of(productUuid);
        productApplicationService.removeProduct(productId);
    }

    @Override
    public MoneyType getSalesPrice(UUID thingId) {
        if (thingId == null)
            throw new DomainValidationException("MyFantasticProductCatalogService", "Thing ID darf nicht null sein.");

        var product = productApplicationService.findById(ProductId.of(thingId));
        return product.getSalesPrice();
    }

    @Override
    public void deleteProductCatalog() {
        productApplicationService.deleteAllProducts();
    }
}