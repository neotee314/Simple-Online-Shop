package com.neotee.ecommercesystem.solution.thing.application;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.solution.thing.domain.Thing;
import com.neotee.ecommercesystem.solution.thing.domain.ThingRepository;
import com.neotee.ecommercesystem.usecases.ThingCatalogUseCases;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.MoneyType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ThingCatalogService implements ThingCatalogUseCases {

    private final ThingService thingService;
    private final ReservationServiceInterface reservationService;
    private final OrderedItemsServiceInterface orderedItemsService;
    private final ThingRepository thingRepository;

    @Override
    public void addThingToCatalog(UUID thingId, String name, String description, Float size,
                                  MoneyType purchasePrice, MoneyType salesPrice) {
        if (thingRepository.findByThingId(thingId) != null) {
            throw new ShopException("Thing with id " + thingId + " already exists");
        }

        Thing thing = new Thing(thingId, name, description, size, 0, (Money) purchasePrice, (Money) salesPrice);
        thingRepository.save(thing);
    }

    @Override
    @Transactional
    public void removeThingFromCatalog(UUID thingId) {
        ThingValidator.validateThingId(thingId);
        Thing thing = thingService.findById(thingId);
        if (thing == null) throw new ShopException("Thing does not exist");

        if (thing.isInStock())
            throw new ShopException("Thing still has inventory");

        if (reservationService.isReservedInBasket(thingId))
            throw new ShopException("Thing is still reserved in a shopping basket");

        if (orderedItemsService.isPartOfCompletedOrder(thingId))
            throw new ShopException("Thing is part of a completed order");

        thingRepository.deleteById(thingId);
    }

    @Override
    public MoneyType getSalesPrice(UUID thingId) {
        ThingValidator.validateThingId(thingId);

        Thing thing = thingService.findById(thingId);
        if (thing == null) throw new ShopException("Thing does not exist");
        return thing.getSalesPrice();
    }

    @Override
    public void deleteThingCatalog() {
        orderedItemsService.deleteOrderParts();
        thingService.deleteAllThing();
    }
}
