package com.neotee.ecommercesystem.shopsystem.storageunit.application.service;

import com.neotee.ecommercesystem.domainprimitives.HomeAddress;
import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.domainprimitives.StorageUnitId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.storageunit.application.port.out.FindProductForStorageUnitPort;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnit;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageUnitApplicationService {

    private final StorageUnitRepository storageUnitRepository;
    private final FindProductForStorageUnitPort findProductPort;

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
        if (address == null)
            throw new DomainValidationException("StorageUnitApplicationService", "Address darf nicht null sein.");
        if (name == null || name.isBlank())
            throw new DomainValidationException("StorageUnitApplicationService", "Name darf nicht leer sein.");

        var storageUnit = StorageUnit.create(address, name);
        return storageUnitRepository.save(storageUnit);
    }

    
    public void addToStock(StorageUnitId storageUnitId, ProductId productId, Integer quantity) {
        if (storageUnitId == null)
            throw new DomainValidationException("StorageUnitApplicationService", "Storage Unit ID darf nicht null sein.");
        if (productId == null)
            throw new DomainValidationException("StorageUnitApplicationService", "Product ID darf nicht null sein.");
        if (quantity == null || quantity <= 0)
            throw new DomainValidationException("StorageUnitApplicationService", "Quantity muss größer als 0 sein.");

        var storageUnit = findById(storageUnitId);
        var product = findProductPort.findById(productId);
        storageUnit.addToStock(product, quantity);
        storageUnitRepository.save(storageUnit);
    }

    
    public void removeFromStock(StorageUnitId storageUnitId, ProductId productId, Integer quantity) {
        if (storageUnitId == null)
            throw new DomainValidationException("StorageUnitApplicationService", "Storage Unit ID darf nicht null sein.");
        if (productId == null)
            throw new DomainValidationException("StorageUnitApplicationService", "Product ID darf nicht null sein.");
        if (quantity == null || quantity <= 0)
            throw new DomainValidationException("StorageUnitApplicationService", "Quantity muss größer als 0 sein.");

        var storageUnit = findById(storageUnitId);
        storageUnit.removeFromStock(productId, quantity);
        storageUnitRepository.save(storageUnit);
    }

    
    public void changeStockTo(StorageUnitId storageUnitId, ProductId productId, Integer newQuantity) {
        if (storageUnitId == null)
            throw new DomainValidationException("StorageUnitApplicationService", "Storage Unit ID darf nicht null sein.");
        if (productId == null)
            throw new DomainValidationException("StorageUnitApplicationService", "Product ID darf nicht null sein.");
        if (newQuantity == null || newQuantity < 0)
            throw new DomainValidationException("StorageUnitApplicationService", "Quantity muss größer oder gleich 0 sein.");

        var storageUnit = findById(storageUnitId);
        var product = findProductPort.findById(productId);
        storageUnit.changeStockTo(product, newQuantity);
        storageUnitRepository.save(storageUnit);
    }

    public Integer getAvailableStock(ProductId productId) {
        if (productId == null)
            throw new DomainValidationException("StorageUnitApplicationService", "Product ID darf nicht null sein.");

        return storageUnitRepository.findAll().stream()
                .mapToInt(storageUnit -> storageUnit.getAvailableStock(productId))
                .sum();
    }

    public Integer getAvailableStockInStorageUnit(StorageUnitId storageUnitId, ProductId productId) {
        if (storageUnitId == null)
            throw new DomainValidationException("StorageUnitApplicationService", "Storage Unit ID darf nicht null sein.");
        if (productId == null)
            throw new DomainValidationException("StorageUnitApplicationService", "Product ID darf nicht null sein.");

        var storageUnit = findById(storageUnitId);
        return storageUnit.getAvailableStock(productId);
    }

    
    public void deleteAllStorageUnits() {
        storageUnitRepository.deleteAll();
    }
}