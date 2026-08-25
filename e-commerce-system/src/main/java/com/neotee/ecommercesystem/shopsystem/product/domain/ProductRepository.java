package com.neotee.ecommercesystem.shopsystem.product.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product, ProductId> {
    @Override
    List<Product> findAll();
    Product findByThingId(ProductId productId);

    List<Product> findByName(String name);
}
