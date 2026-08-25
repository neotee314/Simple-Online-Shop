package com.neotee.ecommercesystem.anticorruption;

import com.neotee.ecommercesystem.domainprimitives.HomeAddress;
import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.domainprimitives.StorageUnitId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.storageunit.application.service.StorageUnitApplicationService;
import com.neotee.ecommercesystem.usecases.StorageUnitUseCases;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.HomeAddressType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MyFantasticStorageUnitUseCaseService implements StorageUnitUseCases {

    private final StorageUnitApplicationService storageUnitApplicationService;

    @Override
    @Transactional
    public UUID addNewStorageUnit(HomeAddressType address, String name) {
        if (address == null)
            throw new DomainValidationException("MyFantasticStorageUnitUseCaseService", "Address darf nicht null sein.");
        if (name == null || name.isBlank())
            throw new DomainValidationException("MyFantasticStorageUnitUseCaseService", "Name darf nicht leer sein.");

        var homeAddress = (HomeAddress) address;
        var storageUnit = storageUnitApplicationService.createStorageUnit(homeAddress, name);
        return storageUnit.getId().getId();
    }

    @Override
    @Transactional
    public void deleteAllStorageUnits() {
        storageUnitApplicationService.deleteAllStorageUnits();
    }

    @Override
    @Transactional
    public void addToStock(UUID storageUnitId, UUID thingId, int addedQuantity) {
        if (storageUnitId == null)
            throw new DomainValidationException("MyFantasticStorageUnitUseCaseService", "Storage Unit ID darf nicht null sein.");
        if (thingId == null)
            throw new DomainValidationException("MyFantasticStorageUnitUseCaseService", "Thing ID darf nicht null sein.");
        if (addedQuantity <= 0)
            throw new DomainValidationException("MyFantasticStorageUnitUseCaseService", "Quantity muss größer als 0 sein.");

        storageUnitApplicationService.addToStock(
                StorageUnitId.of(storageUnitId),
                ProductId.of(thingId),
                addedQuantity
        );
    }

    @Override
    @Transactional
    public void removeFromStock(UUID storageUnitId, UUID thingId, int removedQuantity) {
        if (storageUnitId == null)
            throw new DomainValidationException("MyFantasticStorageUnitUseCaseService", "Storage Unit ID darf nicht null sein.");
        if (thingId == null)
            throw new DomainValidationException("MyFantasticStorageUnitUseCaseService", "Thing ID darf nicht null sein.");
        if (removedQuantity <= 0)
            throw new DomainValidationException("MyFantasticStorageUnitUseCaseService", "Quantity muss größer als 0 sein.");

        storageUnitApplicationService.removeFromStock(
                StorageUnitId.of(storageUnitId),
                ProductId.of(thingId),
                removedQuantity
        );
    }

    @Override
    @Transactional
    public void changeStockTo(UUID storageUnitId, UUID thingId, int newTotalQuantity) {
        if (storageUnitId == null)
            throw new DomainValidationException("MyFantasticStorageUnitUseCaseService", "Storage Unit ID darf nicht null sein.");
        if (thingId == null)
            throw new DomainValidationException("MyFantasticStorageUnitUseCaseService", "Thing ID darf nicht null sein.");
        if (newTotalQuantity < 0)
            throw new DomainValidationException("MyFantasticStorageUnitUseCaseService", "Quantity muss größer oder gleich 0 sein.");

        storageUnitApplicationService.changeStockTo(
                StorageUnitId.of(storageUnitId),
                ProductId.of(thingId),
                newTotalQuantity
        );
    }

    @Override
    public int getAvailableStock(UUID storageUnitId, UUID thingId) {
        if (storageUnitId == null)
            throw new DomainValidationException("MyFantasticStorageUnitUseCaseService", "Storage Unit ID darf nicht null sein.");
        if (thingId == null)
            throw new DomainValidationException("MyFantasticStorageUnitUseCaseService", "Thing ID darf nicht null sein.");

        return storageUnitApplicationService.getAvailableStockInStorageUnit(
                StorageUnitId.of(storageUnitId),
                ProductId.of(thingId)
        );
    }

    @Override
    public int getAvailableStock(UUID thingId) {
        if (thingId == null)
            throw new DomainValidationException("MyFantasticStorageUnitUseCaseService", "Thing ID darf nicht null sein.");

        return storageUnitApplicationService.getAvailableStock(ProductId.of(thingId));
    }
}