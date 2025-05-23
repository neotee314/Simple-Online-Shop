package com.neotee.ecommercesystem.regression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.usecases.*;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.*;
import com.neotee.ecommercesystem.usecases.masterdata.ClientMasterDataInitializer;
import com.neotee.ecommercesystem.usecases.masterdata.Purgatory;
import com.neotee.ecommercesystem.usecases.masterdata.ThingAndStockMasterDataInitializer;


import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.neotee.ecommercesystem.usecases.masterdata.ClientMasterDataInitializer.CLIENT_EMAIL;
import static com.neotee.ecommercesystem.usecases.masterdata.ThingAndStockMasterDataInitializer.THING_DATA;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This test checks the lifecycle of delivery packages, so that e.g.
 * an order is not delivered twice.
 */
@SpringBootTest
public class DeliveryPackagesLifecycleTest {
    @Autowired
    private ClientRegistrationUseCases clientRegistrationUseCases;
    @Autowired
    private ShoppingBasketUseCases shoppingBasketUseCases;
    @Autowired
    private ThingCatalogUseCases thingCatalogUseCases;
    @Autowired
    private StorageUnitUseCases storageUnitUseCases;
    @Autowired
    private DeliveryPackageUseCases deliveryPackageUseCases;
    @Autowired
    private Purgatory purgatory;

    private ClientMasterDataInitializer clientMasterDataInitializer;
    private ThingAndStockMasterDataInitializer thingAndStockMasterDataInitializer;

    private EmailType clientEmail;

    @BeforeEach
    public void setUp() {
        purgatory.deleteEverything();

        clientMasterDataInitializer = new ClientMasterDataInitializer( clientRegistrationUseCases );
        clientMasterDataInitializer.registerAllClients();

        thingAndStockMasterDataInitializer = new ThingAndStockMasterDataInitializer(
                thingCatalogUseCases, storageUnitUseCases );
        thingAndStockMasterDataInitializer.addAllThings();
        thingAndStockMasterDataInitializer.addAllStorageUnits();
        thingAndStockMasterDataInitializer.addAllStock();

        // put things in the shopping basket ...
        clientEmail = CLIENT_EMAIL[2];
        shoppingBasketUseCases.addThingToShoppingBasket(
                clientEmail, (UUID) THING_DATA[3][0], 2 );
        shoppingBasketUseCases.addThingToShoppingBasket(
                clientEmail, (UUID) THING_DATA[2][0], 1 );
    }


    @Test
    public void testValidOrderHasValidDeliveryPackages() {
        // given
        UUID orderId = shoppingBasketUseCases.checkout( clientEmail );
        assertNotNull( orderId );

        // when
        List<UUID> storageUnits =
                deliveryPackageUseCases.getContributingStorageUnitsForOrder( orderId );

        // then
        assertNotNull( storageUnits );
        assertTrue( storageUnits.size() > 0 );
        Map<UUID, Integer> deliveryPackage =
                deliveryPackageUseCases.getDeliveryPackageForOrderAndStorageUnit(
                        orderId, storageUnits.get( 0 ) );
        assertNotNull( deliveryPackage );
        assertTrue( deliveryPackage.size() > 0 );
    }


    @Test
    public void testInvalidParameters() {
        // given
        // when
        assertThrows( ShopException.class,
                () -> deliveryPackageUseCases.getContributingStorageUnitsForOrder( UUID.randomUUID() ) );
        assertThrows( ShopException.class,
                () -> deliveryPackageUseCases.getContributingStorageUnitsForOrder( null ) );
        assertThrows( ShopException.class,
                () -> deliveryPackageUseCases.
                        getDeliveryPackageForOrderAndStorageUnit( null, null ) );
        assertThrows( ShopException.class,
                () -> deliveryPackageUseCases.
                        getDeliveryPackageForOrderAndStorageUnit( null, UUID.randomUUID() ) );
        assertThrows( ShopException.class,
                () -> deliveryPackageUseCases.
                        getDeliveryPackageForOrderAndStorageUnit( UUID.randomUUID(), null ) );
        assertThrows( ShopException.class,
                () -> deliveryPackageUseCases.
                        getDeliveryPackageForOrderAndStorageUnit( UUID.randomUUID(), UUID.randomUUID() ) );
    }
}
