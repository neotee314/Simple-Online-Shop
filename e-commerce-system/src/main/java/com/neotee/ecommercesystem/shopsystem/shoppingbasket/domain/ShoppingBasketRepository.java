package com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain;

import com.neotee.ecommercesystem.domainprimitives.ClientId;
import com.neotee.ecommercesystem.domainprimitives.ShoppingBasketId;
import com.neotee.ecommercesystem.shopsystem.client.domain.Client;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ShoppingBasketRepository extends CrudRepository<ShoppingBasket, ShoppingBasketId> {
    @Override
    List<ShoppingBasket> findAll();

    Optional<ShoppingBasket> findByClientEmail(EmailType clientEmail);

    Optional<ShoppingBasket> findByClient(Client client);

    @Query("SELECT sb FROM ShoppingBasket sb WHERE sb.client.id = :clientId")
    Optional<ShoppingBasket> findByClientId(ClientId clientId);
}


