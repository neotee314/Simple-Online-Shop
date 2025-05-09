package com.neotee.ecommercesystem.solution.storageunit.application;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.HomeAddress;
import com.neotee.ecommercesystem.solution.shoppingbasket.application.ReservationService;
import com.neotee.ecommercesystem.solution.storageunit.domain.StorageUnit;
import com.neotee.ecommercesystem.solution.storageunit.domain.StorageUnitRepository;
import com.neotee.ecommercesystem.solution.thing.application.ThingService;
import com.neotee.ecommercesystem.usecases.StorageUnitUseCases;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.HomeAddressType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageUnitUseCasesService implements StorageUnitUseCases {

    private final StorageUnitRepository storageUnitRepository;
    private final ThingService thingService;
    private final ReservationService reservationService;

    @Override
    public UUID addNewStorageUnit(HomeAddressType address, String name) {
        StorageUnit storageUnit = new StorageUnit((HomeAddress) address, name);
        storageUnitRepository.save(storageUnit);
        return storageUnit.getStorageId();
    }

    @Override
    public void deleteAllStorageUnits() {
        storageUnitRepository.deleteAll();
    }

    @Override
    @Transactional
    public void addToStock(UUID storageUnitId, UUID thingId, int addedQuantity) {
        thingService.addToStock(thingId, addedQuantity);
        StorageUnit storageUnit = storageUnitRepository.findById(storageUnitId).orElseThrow(() -> new ShopException("Storage with Id " + storageUnitId + " does not exist"));
        storageUnit.addToStock(thingId, addedQuantity);
        storageUnitRepository.save(storageUnit);
    }

    @Override
    @Transactional
    public void removeFromStock(UUID storageUnitId, UUID thingId, int removedQuantity) {
        StorageUnitValidator.validateStorageUnitId(storageUnitId);
        StorageUnitValidator.validateQuantityNotNegative(removedQuantity);

        StorageUnit storageUnit = storageUnitRepository.findById(storageUnitId).orElseThrow(() -> new ShopException("Storage with Id " + storageUnitId + " does not exist"));
        int currentStock = storageUnit.getAvailableStock(thingId);
        int reserved = reservationService.getTotalReservedInAllBaskets(thingId);
        int totalRemovable = currentStock + reserved;

        if (removedQuantity > totalRemovable) {
            throw new ShopException("The removed quantity exceeds available and reserved stock.");
        }

        int removeFromStock = Math.min(currentStock, removedQuantity);
        if (removeFromStock > 0) {
            storageUnit.removeFromStock(thingId, removeFromStock);
            thingService.removeFromStock(thingId, removeFromStock);
        }

        int removeFromReserved = removedQuantity - removeFromStock;
        if (removeFromReserved > 0) {
            reservationService.removeFromReservedQuantity(thingId, removeFromReserved);
            thingService.removeFromStock(thingId, removeFromReserved);
        }

        storageUnitRepository.save(storageUnit);
    }

    @Override
    @Transactional
    public void changeStockTo(UUID storageUnitId, UUID thingId, int newTotalQuantity) {
        StorageUnitValidator.validateStorageUnitId(storageUnitId);
        StorageUnitValidator.validateQuantityNotNegative(newTotalQuantity);
        if(!thingService.existsById(thingId)) throw new ShopException("Thing does not exist");

        StorageUnit storageUnit = storageUnitRepository.findById(storageUnitId).orElseThrow(() -> new ShopException("Storage with Id " + storageUnitId + " does not exist"));
        int reserved = reservationService.getTotalReservedInAllBaskets(thingId);

        if (newTotalQuantity < reserved) {
            int toRemove = reserved - newTotalQuantity;
            reservationService.removeFromReservedQuantity(thingId, toRemove);
        }

        storageUnit.changeStockTo(thingId, newTotalQuantity);
        thingService.changeStockTo(thingId,newTotalQuantity);
        storageUnitRepository.save(storageUnit);
    }

    @Override
    @Transactional
    public int getAvailableStock(UUID storageUnitId, UUID thingId) {
        StorageUnitValidator.validateStorageUnitId(storageUnitId);
        StorageUnit storageUnit = storageUnitRepository.findById(storageUnitId).orElseThrow(() -> new ShopException("Storage with Id " + storageUnitId + " does not exist"));
        return storageUnit.getAvailableStock(thingId);
    }

    @Override
    @Transactional
    public int getAvailableStock(UUID thingId) {
        List<StorageUnit> storageUnits = new ArrayList<>();
        storageUnitRepository.findAll().forEach(storageUnits::add);
        if (storageUnits.isEmpty()) throw new ShopException("No storage units exist");

        return storageUnits.stream()
                .mapToInt(storageUnit -> storageUnit.getAvailableStock(thingId))
                .sum();
    }
}
