package com.neotee.ecommercesystem.shopsystem.product.domain;

import com.neotee.ecommercesystem.domainprimitives.ProductId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product, ProductId> {
    @Override
    List<Product> findAll();
    List<Product> findByName(String name);
}
