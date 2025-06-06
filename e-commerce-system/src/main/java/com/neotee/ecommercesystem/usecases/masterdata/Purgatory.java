package com.neotee.ecommercesystem.usecases.masterdata;

import com.neotee.ecommercesystem.usecases.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * Removes everything :-)
 */
@Service
@Slf4j
public class Purgatory {
    @Autowired
    private ClientRegistrationUseCases clientRegistrationUseCases;
    @Autowired
    private ShoppingBasketUseCases shoppingBasketUseCases;
    @Autowired
    private OrderUseCases orderUseCases;
    @Autowired
    private ThingCatalogUseCases thingCatalogUseCases;
    @Autowired
    private StorageUnitUseCases storageUnitUseCases;
    @Autowired
    private DeliveryPackageUseCases deliveryPackageUseCases;

    public void deleteEverything() {
        log.info("Purgatory: Deleting everything...");
        deliveryPackageUseCases.deleteAllDeliveryPackages();
        orderUseCases.deleteAllOrders();
        shoppingBasketUseCases.emptyAllShoppingBaskets();
        storageUnitUseCases.deleteAllStorageUnits();
        clientRegistrationUseCases.deleteAllClients();
        thingCatalogUseCases.deleteThingCatalog();
    }
}
