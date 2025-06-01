package com.neotee.ecommercesystem.solution.thing.application.service;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.solution.thing.domain.Thing;
import com.neotee.ecommercesystem.solution.thing.domain.ThingId;
import com.neotee.ecommercesystem.solution.thing.domain.ThingRepository;
import com.neotee.ecommercesystem.usecases.ThingCatalogUseCases;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.MoneyType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ThingCatalogService implements ThingCatalogUseCases {

    private final ThingService thingService;
    private final ReservationCheckServiceInterface reservationService;
    private final OrderedItemsServiceInterface orderedItemsService;
    private final InventoryServiceInterface inventoryService;
    private final ThingRepository thingRepository;

    @Override
    public void addThingToCatalog(UUID thingId, String name, String description, Float size,
                                  MoneyType purchasePrice, MoneyType salesPrice) {
        if (thingService.existsById(thingId)) {
            throw new ShopException("Thing with id " + thingId + " already exists");
        }

        Thing thing = new Thing(thingId, name, description, size, (Money) purchasePrice, (Money) salesPrice);
        thingRepository.save(thing);
    }

    @Override
    @Transactional
    public void removeThingFromCatalog(UUID thingId) {
        ThingValidator.validateThingId(thingId);
        Thing thing = thingService.findById(thingId);
        if (thing == null) throw new ShopException("Thing does not exist");

        if (inventoryService.isInStock(thing.getThingId().getId()))
            throw new ShopException("Thing still has inventory");

        if (reservationService.isReservedInAnyBasket(thingId))
            throw new ShopException("Thing is still reserved in a shopping basket");

        if (orderedItemsService.isPartOfCompletedOrder(thingId))
            throw new ShopException("Thing is part of a completed order");

        thingRepository.deleteById(new ThingId(thingId));
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
        thingRepository.deleteAll();
    }
}
