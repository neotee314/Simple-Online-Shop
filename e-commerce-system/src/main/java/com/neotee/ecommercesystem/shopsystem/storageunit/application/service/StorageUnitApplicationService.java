package com.neotee.ecommercesystem.shopsystem.storageunit.application.service;

import com.neotee.ecommercesystem.domainprimitives.HomeAddress;
import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.domainprimitives.StorageUnitId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.product.application.service.ProductApplicationService;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.model.StorageUnit;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.repository.StorageUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageUnitApplicationService {

    private final StorageUnitRepository storageUnitRepository;
    private final ProductApplicationService productApplicationService;
    private final ApplicationEventPublisher eventPublisher;

    public StorageUnit findById(StorageUnitId storageUnitId) {
        if (storageUnitId == null)
            throw new DomainValidationException("StorageUnitApplicationService", "Storage Unit ID darf nicht null sein.");

        return storageUnitRepository.findById(storageUnitId)
                .orElseThrow(() -> new DomainValidationException("StorageUnitApplicationService", "Storage Unit nicht gefunden."));
    }

    public List<StorageUnit> findAll() {
        return storageUnitRepository.findAll();
    }


    public StorageUnit createStorageUnit(HomeAddress address, String name) {
        var storageUnit = StorageUnit.create(address, name);
        return storageUnitRepository.save(storageUnit);
    }


    public void addToStock(StorageUnitId storageUnitId, ProductId productId, Integer quantity) {
        var storageUnit = findById(storageUnitId);
        var product = productApplicationService.findById(productId);
        storageUnit.addToStock(product, quantity);
        storageUnitRepository.save(storageUnit);
    }


    public void removeFromStock(StorageUnitId storageUnitId, ProductId productId, Integer quantity) {
        var storageUnit = findById(storageUnitId);
        var product = productApplicationService.findById(productId);
        storageUnit.removeFromStock(product, quantity);
        storageUnitRepository.save(storageUnit);

        for (Object event : storageUnit.getDomainEvents()) {
            eventPublisher.publishEvent(event);
        }
        storageUnit.clearEvents();
    }


    public void changeStockTo(StorageUnitId storageUnitId, ProductId productId, Integer newQuantity) {
        var storageUnit = findById(storageUnitId);
        var product = productApplicationService.findById(productId);
        storageUnit.changeStockTo(product, newQuantity);
        storageUnitRepository.save(storageUnit);
        publishEvents(storageUnit);

        for (Object event : storageUnit.getDomainEvents()) {
            eventPublisher.publishEvent(event);
        }
        storageUnit.clearEvents();

    }

    public Integer getAvailableStock(ProductId productId) {
        var product = productApplicationService.findById(productId);
        var storageUnits = storageUnitRepository.findAll();
        if (storageUnits.isEmpty())
            throw new DomainValidationException("StorageUnitApplicationService", "No storage units available");

        return storageUnits.stream()
                .mapToInt(storageUnit -> storageUnit.getAvailableStock(product))
                .sum();
    }

    public Integer getAvailableStockInStorageUnit(StorageUnitId storageUnitId, ProductId productId) {
        var storageUnit = findById(storageUnitId);
        var product = productApplicationService.findById(productId);
        return storageUnit.getAvailableStock(product);
    }


    public void deleteAllStorageUnits() {
        storageUnitRepository.deleteAll();
    }

    private void publishEvents(StorageUnit storageUnit) {
        for (Object event : storageUnit.getDomainEvents()) {
            eventPublisher.publishEvent(event);
        }
        storageUnit.clearEvents();
    }
}