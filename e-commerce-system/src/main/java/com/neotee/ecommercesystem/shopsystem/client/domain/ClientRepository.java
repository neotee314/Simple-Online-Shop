package com.neotee.ecommercesystem.shopsystem.client.domain;

import com.neotee.ecommercesystem.domainprimitives.ClientId;
import com.neotee.ecommercesystem.domainprimitives.Email;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends CrudRepository<Client, ClientId> {

    Optional<Client> findByEmail(Email email);

    @Override
    List<Client> findAll();
}