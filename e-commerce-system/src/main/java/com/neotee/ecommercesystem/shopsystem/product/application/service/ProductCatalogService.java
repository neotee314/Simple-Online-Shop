package com.neotee.ecommercesystem.shopsystem.product.application.service;

import com.neotee.ecommercesystem.exception.ShopException;
import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.product.domain.ProductId;
import com.neotee.ecommercesystem.shopsystem.product.domain.ProductRepository;
import com.neotee.ecommercesystem.usecases.ThingCatalogUseCases;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.MoneyType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductCatalogService implements ThingCatalogUseCases {

    private final ProductService productService;
    private final ReservationCheckServiceInterface reservationService;
    private final OrderedItemsServiceInterface orderedItemsService;
    private final InventoryServiceInterface inventoryService;
    private final ProductRepository productRepository;

    @Override
    public void addThingToCatalog(UUID thingId, String name, String description, Float size,
                                  MoneyType purchasePrice, MoneyType salesPrice) {
        if (productService.existsById(thingId)) {
            throw new ShopException("Thing with id " + thingId + " already exists");
        }

        Product product = new Product(thingId, name, description, size, (Money) purchasePrice, (Money) salesPrice);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void removeThingFromCatalog(UUID thingId) {
        ProductValidator.validateThingId(thingId);
        Product product = productService.findById(thingId);
        if (product == null) throw new ShopException("Thing does not exist");

        if (inventoryService.isInStock(product.getProductId().getId()))
            throw new ShopException("Thing still has inventory");

        if (reservationService.isReservedInAnyBasket(thingId))
            throw new ShopException("Thing is still reserved in a shopping basket");

        if (orderedItemsService.isPartOfCompletedOrder(thingId))
            throw new ShopException("Thing is part of a completed order");

        productRepository.deleteById(new ProductId(thingId));
    }

    @Override
    public MoneyType getSalesPrice(UUID thingId) {
        ProductValidator.validateThingId(thingId);

        Product product = productService.findById(thingId);
        if (product == null) throw new ShopException("Thing does not exist");
        return product.getSalesPrice();
    }

    @Override
    public void deleteThingCatalog() {
        productRepository.deleteAll();
    }
}
