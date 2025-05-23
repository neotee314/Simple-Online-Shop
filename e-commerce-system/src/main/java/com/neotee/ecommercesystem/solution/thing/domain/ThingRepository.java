package com.neotee.ecommercesystem.solution.thing.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ThingRepository extends CrudRepository<Thing, UUID> {
    @Override
    List<Thing> findAll();
    Thing findByThingId(UUID id);
}
