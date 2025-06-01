package com.neotee.ecommercesystem.solution.client.domain;

import com.neotee.ecommercesystem.domainprimitives.Email;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends CrudRepository<Client, ClientId> {
    Client findByEmail( Email email);
    @Override
    List<Client> findAll();
}