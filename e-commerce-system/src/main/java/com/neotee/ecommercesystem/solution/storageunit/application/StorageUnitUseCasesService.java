package com.neotee.ecommercesystem.solution.storageunit.application;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.HomeAddress;
import com.neotee.ecommercesystem.solution.storageunit.domain.RemovalPlan;
import com.neotee.ecommercesystem.solution.storageunit.domain.StorageUnit;
import com.neotee.ecommercesystem.solution.storageunit.domain.StorageUnitRepository;
import com.neotee.ecommercesystem.usecases.StorageUnitUseCases;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.HomeAddressType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageUnitUseCasesService implements StorageUnitUseCases {

    private final StorageUnitRepository storageUnitRepository;
    private final ReservationServiceInterface reservationServiceInterface;
    private final StockServiceInterface stockServiceInterface;

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
        StorageUnit storageUnit = storageUnitRepository.findById(storageUnitId)
                .orElseThrow(() -> new ShopException("Storage with Id " + storageUnitId + " does not exist"));
        storageUnit.addToStock(thingId, addedQuantity);
        stockServiceInterface.addToStock(thingId, addedQuantity);
        storageUnitRepository.save(storageUnit);
    }

    @Override
    @Transactional
    public void removeFromStock(UUID storageUnitId, UUID thingId, int removedQuantity) {
        StorageUnit storageUnit =storageUnitRepository.findById(storageUnitId)
                .orElseThrow(() -> new ShopException("Storage with Id " + storageUnitId + " does not exist"));
        int reserved = reservationServiceInterface.getTotalReservedInAllBaskets(thingId);

        RemovalPlan removalPlan = storageUnit.planRemoval(thingId, removedQuantity, reserved);

        if (removalPlan.fromStock() > 0) {
            storageUnit.removeFromStock(thingId, removalPlan.fromStock());
            stockServiceInterface.removeFromStock(thingId, removalPlan.fromStock());
        }

        if (removalPlan.fromReserved() > 0) {
            reservationServiceInterface.removeFromReservedQuantity(thingId, removalPlan.fromReserved());
            stockServiceInterface.removeFromStock(thingId, removalPlan.fromReserved());
        }

        storageUnitRepository.save(storageUnit);
    }

    @Override
    @Transactional
    public void changeStockTo(UUID storageUnitId, UUID thingId, int newTotalQuantity) {
        if (!stockServiceInterface.existsById(thingId)) {
            throw new ShopException("Thing does not exist");
        }

        StorageUnit storageUnit = storageUnitRepository.findById(storageUnitId)
                .orElseThrow(() -> new ShopException("Storage with Id " + storageUnitId + " does not exist"));
        int reserved = reservationServiceInterface.getTotalReservedInAllBaskets(thingId);

        if (newTotalQuantity < reserved) {
            int toRemove = reserved - newTotalQuantity;
            reservationServiceInterface.removeFromReservedQuantity(thingId, toRemove);
        }

        storageUnit.changeStockTo(thingId, newTotalQuantity);
        stockServiceInterface.changeStockTo(thingId, newTotalQuantity);
        storageUnitRepository.save(storageUnit);
    }

    @Override
    @Transactional(readOnly = true)
    public int getAvailableStock(UUID storageUnitId, UUID thingId) {
        StorageUnitValidator.validateStorageUnitId(storageUnitId);
        StorageUnit storageUnit = storageUnitRepository.findById(storageUnitId)
                .orElseThrow(() -> new ShopException("Storage with Id " + storageUnitId + " does not exist"));
        return storageUnit.getAvailableStock(thingId);
    }

    @Override
    @Transactional(readOnly = true)
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
