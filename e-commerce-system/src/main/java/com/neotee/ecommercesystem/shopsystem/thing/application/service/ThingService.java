package com.neotee.ecommercesystem.shopsystem.thing.application.service;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.shopsystem.thing.domain.Thing;
import com.neotee.ecommercesystem.shopsystem.thing.domain.ThingId;
import com.neotee.ecommercesystem.shopsystem.thing.domain.ThingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ThingService{

    private final ThingRepository thingRepository;


    public Thing findById(UUID thingId) {
        return thingRepository.findById(new ThingId(thingId)).orElse(null);
    }

    public void deleteAllThing() {
        thingRepository.deleteAll();
    }



    public Money getSalesPrice(UUID thingId) {
        ThingValidator.validateThingId(thingId);
        Thing thing = findById(thingId);
        if (thing == null) throw new ShopException("Thing does not exist");
        return thing.getSalesPrice();
    }

    public List<UUID> findAll() {
        return thingRepository.findAll().stream()
                .map(thing -> thing.getThingId().getId())
                .toList();
    }


    public boolean existsById(UUID thingId) {
        ThingValidator.validateThingId(thingId);
        return thingRepository.existsById(new ThingId(thingId));
    }

}

