package com.neotee.ecommercesystem.solution.shoppingbasket.domain;

import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShoppingBasketRepository extends CrudRepository<ShoppingBasket, UUID> {
    List<ShoppingBasket> findAll();

    Optional<ShoppingBasket> findByClientEmail(EmailType clientEmail);
}


