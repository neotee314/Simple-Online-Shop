package com.neotee.ecommercesystem.shopsystem.storageunit.application.service;

import com.neotee.ecommercesystem.domainprimitives.StorageUnitId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.exceptions.EntityNotFoundException;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.port.out.FindStorageUnitsPort;
import com.neotee.ecommercesystem.shopsystem.deliverypackage.application.port.out.UpdateStorageUnitPort;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnit;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StorageUnitPortAdapter implements FindStorageUnitsPort, UpdateStorageUnitPort {

    private final StorageUnitRepository storageUnitRepository;

    @Override
    public List<StorageUnit> findAll() {
        var storageUnits = storageUnitRepository.findAll();
        if (storageUnits.isEmpty())
            throw new DomainValidationException("StorageUnitPortAdapter", "Keine Storage Units gefunden.");
        return storageUnits;
    }

    @Override
    public StorageUnit findById(StorageUnitId storageUnitId) {
        return storageUnitRepository.findById(storageUnitId).orElseThrow(() ->
                new EntityNotFoundException("StorageUnitPortAdapter", "Keine Storage Units gefunden."));
    }

    @Override
    public void update(StorageUnit storageUnit) {
        if (storageUnit == null)
            throw new DomainValidationException("StorageUnitPortAdapter", "Storage Unit darf nicht null sein.");

        storageUnitRepository.save(storageUnit);
    }
}