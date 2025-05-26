package com.neotee.ecommercesystem.solution.thing.application.service;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.solution.storageunit.application.service.StockServiceInterface;
import com.neotee.ecommercesystem.solution.thing.domain.Thing;
import com.neotee.ecommercesystem.solution.thing.domain.ThingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ThingService implements StockServiceInterface {

    private final ThingRepository thingRepository;


    public Thing findById(UUID thingId) {
        return thingRepository.findById(thingId).orElse(null);
    }

    public void deleteAllThing() {
        thingRepository.deleteAll();
    }

    @Override
    @Transactional
    public void addToStock(UUID thingId, int quantity) {
        Thing thing = findById(thingId);
        if (thing == null) throw new ShopException("Thing does not exist");
        thing.addToStock(quantity);
        thingRepository.save(thing);
    }

    @Override
    @Transactional
    public void removeFromStock(UUID thingId, int quantity) {
        ThingValidator.validateThingId(thingId);
        Thing thing = findById(thingId);
        if (thing == null) throw new ShopException("Thing doesent exist");
        thing.removeFromStock(quantity);
        thingRepository.save(thing);
    }

    public Money getSalesPrice(UUID thingId) {
        ThingValidator.validateThingId(thingId);
        Thing thing = findById(thingId);
        if (thing == null) throw new ShopException("Thing does not exist");
        return thing.getSalesPrice();
    }

    public List<UUID> findAll() {
        return thingRepository.findAll().stream()
                .map(Thing::getThingId)
                .toList();
    }
    @Override
    public boolean existsById(UUID thingId) {
        ThingValidator.validateThingId(thingId);
        return thingRepository.existsById(thingId);
    }
    @Override
    public void changeStockTo(UUID thingId, int newTotalQuantity) {
        ThingValidator.validateThingId(thingId);
        Thing thing = findById(thingId);
        if (thing == null) throw new ShopException("Thing does not exist");
        thing.changeStockTo(newTotalQuantity);
        thingRepository.save(thing);
    }

    public int getAvailableInventory(UUID thingId) {
        if(thingId==null) throw new ShopException("thingid cannot be null");
        Thing thing = thingRepository.findByThingId(thingId);
        if(thing==null) throw new ShopException("Thing not found");
        return thing.getStockQuantity();
    }
}

