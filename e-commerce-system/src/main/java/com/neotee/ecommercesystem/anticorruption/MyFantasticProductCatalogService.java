package com.neotee.ecommercesystem.anticorruption;

import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.domainprimitives.ProductId;
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
        var productId = ProductId.of(productUuid);
        productApplicationService.removeProduct(productId);
    }

    @Override
    public MoneyType getSalesPrice(UUID thingId) {
        var product = productApplicationService.findById(ProductId.of(thingId));
        return product.getSalesPrice();
    }

    @Override
    public void deleteProductCatalog() {
        productRepository.deleteAll();
    }
}