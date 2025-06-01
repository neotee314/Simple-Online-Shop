package com.neotee.ecommercesystem.shopsystem.thing.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThingRepository extends CrudRepository<Thing, ThingId> {
    @Override
    List<Thing> findAll();
    Thing findByThingId(ThingId thingId);
}
