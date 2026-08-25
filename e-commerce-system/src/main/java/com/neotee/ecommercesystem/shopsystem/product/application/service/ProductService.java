package com.neotee.ecommercesystem.shopsystem.product.application.service;

import com.neotee.ecommercesystem.exception.ShopException;
import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import com.neotee.ecommercesystem.shopsystem.product.domain.ProductId;
import com.neotee.ecommercesystem.shopsystem.product.domain.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;


    public Product findById(UUID thingId) {
        return productRepository.findById(new ProductId(thingId)).orElse(null);
    }

    public void deleteAllThing() {
        productRepository.deleteAll();
    }



    public Money getSalesPrice(UUID thingId) {
        ProductValidator.validateThingId(thingId);
        Product product = findById(thingId);
        if (product == null) throw new ShopException("Thing does not exist");
        return product.getSalesPrice();
    }

    public List<UUID> findAll() {
        return productRepository.findAll().stream()
                .map(thing -> thing.getProductId().getId())
                .toList();
    }


    public boolean existsById(UUID thingId) {
        ProductValidator.validateThingId(thingId);
        return productRepository.existsById(new ProductId(thingId));
    }

}

