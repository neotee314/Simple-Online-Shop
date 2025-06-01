package com.neotee.ecommercesystem.solution.storageunit.application.service;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.HomeAddress;
import com.neotee.ecommercesystem.exception.EntityNotFoundException;
import com.neotee.ecommercesystem.solution.storageunit.domain.StockLevelRepository;
import com.neotee.ecommercesystem.solution.storageunit.domain.StorageUnit;
import com.neotee.ecommercesystem.solution.storageunit.domain.StorageUnitId;
import com.neotee.ecommercesystem.solution.storageunit.domain.StorageUnitRepository;
import com.neotee.ecommercesystem.solution.thing.application.service.ThingService;
import com.neotee.ecommercesystem.solution.thing.domain.Thing;
import com.neotee.ecommercesystem.solution.thing.domain.ThingId;
import com.neotee.ecommercesystem.usecases.StorageUnitUseCases;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.HomeAddressType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageUnitUseCasesService implements StorageUnitUseCases {

    private final StorageUnitRepository storageUnitRepository;
    private final ReservedQuantityService reservationServiceInterface;
    private final StockLevelRepository stockLevelRepository;
    private final ThingService thingService;

    @Override
    public UUID addNewStorageUnit(HomeAddressType address, String name) {
        StorageUnit storageUnit = new StorageUnit((HomeAddress) address, name);
        storageUnitRepository.save(storageUnit);
        return storageUnit.getUUID();
    }

    @Override
    public void deleteAllStorageUnits() {
        storageUnitRepository.deleteAll();
    }

    @Override
    @Transactional
    public void addToStock(UUID storageUnitId, UUID thingId, int addedQuantity) {
        StorageUnit storageUnit = storageUnitRepository.findById(new StorageUnitId(storageUnitId))
                .orElseThrow(EntityNotFoundException::new);
        Thing thing = thingService.findById(thingId);
        storageUnit.addToStock(thing, addedQuantity);
        storageUnitRepository.save(storageUnit);
    }

    @Override
    @Transactional
    public void removeFromStock(UUID storageUnitId, UUID thingId, int removedQuantity) {
        StorageUnit storageUnit = storageUnitRepository.findById(new StorageUnitId(storageUnitId))
                .orElseThrow(EntityNotFoundException::new);
        int reserved = reservationServiceInterface.getTotalReservedInAllBaskets(thingId);
        ThingId id = new ThingId(thingId);
        int currentInventory = storageUnit.getQuantityOf(id);

        if (removedQuantity > currentInventory + reserved)
            throw new ShopException("The removed quantity is greater than the available quantity");

        int stockAfter = currentInventory - removedQuantity;

        if (stockAfter < reserved) {
            reservationServiceInterface.removeFromReservedQuantity(id.getId(), removedQuantity);
            return;
        }

        storageUnit.removeFromStock(id, removedQuantity);
        storageUnitRepository.save(storageUnit);

    }

    @Override
    @Transactional
    public void changeStockTo(UUID storageUnitId, UUID thingId, int newTotalQuantity) {
        StorageUnit storageUnit = storageUnitRepository.findById(new StorageUnitId(storageUnitId))
                .orElseThrow(EntityNotFoundException::new);
        int reserved = reservationServiceInterface.getTotalReservedInAllBaskets(thingId);
        Thing thing = thingService.findById(thingId);
        if (thing == null) throw new EntityNotFoundException();

        if (newTotalQuantity < reserved) {
            int toRemove = reserved - newTotalQuantity;
            reservationServiceInterface.removeFromReservedQuantity(thingId, toRemove);
        }

        storageUnit.changeStockTo(thing, newTotalQuantity);
        storageUnitRepository.save(storageUnit);
    }

    @Override
    @Transactional
    public int getAvailableStock(UUID storageUnitId, UUID thingId) {
        StorageUnit storageUnit = storageUnitRepository.findById(new StorageUnitId(storageUnitId))
                .orElseThrow(EntityNotFoundException::new);
        return storageUnit.getAvailableStock(thingId);
    }

    @Override
    @Transactional
    public int getAvailableStock(UUID thingId) {
        List<StorageUnit> storageUnits = storageUnitRepository.findAll();

        if (storageUnits.isEmpty()) {
            throw new ShopException("No storage units exist");
        }

        return storageUnits.stream()
                .mapToInt(unit -> unit.getAvailableStock(thingId))
                .sum();
    }

}
