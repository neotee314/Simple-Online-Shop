package com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingBasketPartRepository extends CrudRepository<ShoppingBasketPart, ShoppingBasketPartId> {
}
