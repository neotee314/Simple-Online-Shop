package com.neotee.ecommercesystem.regression;

import com.neotee.ecommercesystem.config.TestContainersConfiguration;
import com.neotee.ecommercesystem.exceptions.ShopException;
import com.neotee.ecommercesystem.usecases.*;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import com.neotee.ecommercesystem.usecases.masterdata.Purgatory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.neotee.ecommercesystem.helper.ClientMasterDataInitializer.CLIENT_EMAIL;
import com.neotee.ecommercesystem.helper.ClientMasterDataInitializer;
import com.neotee.ecommercesystem.helper.ThingAndStockMasterDataInitializer;
import org.springframework.context.annotation.Import;

import static com.neotee.ecommercesystem.helper.FactoryMethodInvoker.instantiateEmail;
import static com.neotee.ecommercesystem.helper.ThingAndStockMasterDataInitializer.THING_DATA;
import static com.neotee.ecommercesystem.helper.ThingAndStockMasterDataInitializer.THING_STOCK;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import(TestContainersConfiguration.class)
public class ShoppingBasketTest {
    @Autowired
    private ClientRegistrationUseCases clientRegistrationUseCases;
    @Autowired
    private ShoppingBasketUseCases shoppingBasketUseCases;
    @Autowired
    private ProductCatalogUseCases productCatalogUseCases;
    @Autowired
    private StorageUnitUseCases storageUnitUseCases;
    @Autowired
    private OrderUseCases orderUseCases;
    @Autowired
    private Purgatory purgatory;

    private EmailType nonExistingEmail;

    private ClientMasterDataInitializer clientMasterDataInitializer;
    private ThingAndStockMasterDataInitializer thingAndStockMasterDataInitializer;

    @BeforeEach
    public void setUp() {
        purgatory.deleteEverything();

        clientMasterDataInitializer = new ClientMasterDataInitializer( clientRegistrationUseCases );
        clientMasterDataInitializer.registerAllClients();

        thingAndStockMasterDataInitializer = new ThingAndStockMasterDataInitializer(
                productCatalogUseCases, storageUnitUseCases );
        thingAndStockMasterDataInitializer.addAllThings();
        thingAndStockMasterDataInitializer.addAllStorageUnits();
        thingAndStockMasterDataInitializer.addAllStock();

        nonExistingEmail = instantiateEmail( "this@nononono.de" );
    }


    @Test
    public void testInvalidRemoveFromShoppingBasket() {
        // given
        UUID nonExistentThingId = UUID.randomUUID();
        UUID thingId5 = (UUID) THING_DATA[5][0];
        UUID thingId0 = (UUID) THING_DATA[0][0];
        UUID thingId1 = (UUID) THING_DATA[1][0];
        UUID thingId2 = (UUID) THING_DATA[2][0];
        EmailType clientEmail0 = CLIENT_EMAIL[0];
        shoppingBasketUseCases.addProductToShoppingBasket(
                clientEmail0, thingId1, 5 );
        shoppingBasketUseCases.addProductToShoppingBasket(
                clientEmail0, thingId2, 15 );

        // when
        shoppingBasketUseCases.removeProductFromShoppingBasket(
                clientEmail0, thingId1, 2 );
        shoppingBasketUseCases.removeProductFromShoppingBasket(
                clientEmail0, thingId2, 4 );
        shoppingBasketUseCases.removeProductFromShoppingBasket(
                clientEmail0, thingId2, 7 );

        // then
        assertThrows( ShopException.class,
                () -> shoppingBasketUseCases.removeProductFromShoppingBasket(
                        clientEmail0, nonExistentThingId, 12 ) );
        assertThrows( ShopException.class,
                () -> shoppingBasketUseCases.removeProductFromShoppingBasket(
                        nonExistingEmail, thingId5, 12 ) );
        assertThrows( ShopException.class,
                () -> shoppingBasketUseCases.removeProductFromShoppingBasket(
                        clientEmail0, thingId5, -1 ) );
        assertThrows( ShopException.class,
                () -> shoppingBasketUseCases.removeProductFromShoppingBasket(
                        clientEmail0, thingId0, 1 ) );
        assertThrows( ShopException.class,
                () -> shoppingBasketUseCases.removeProductFromShoppingBasket(
                        clientEmail0, thingId1, 4 ) );
        assertThrows( ShopException.class,
                () -> shoppingBasketUseCases.removeProductFromShoppingBasket(
                        clientEmail0, thingId2, 5 ) );
    }



    @Test
    public void testCheckout() {
        // given
        UUID thingId1 = (UUID) THING_DATA[1][0];
        UUID thingId2 = (UUID) THING_DATA[2][0];
        UUID thingId3 = (UUID) THING_DATA[3][0];
        int stock1before = THING_STOCK.get( thingId1 )[0];
        int stock2before = THING_STOCK.get( thingId2 )[0];
        int stock3before = THING_STOCK.get( thingId3 )[0];
        EmailType clientEmail3 = CLIENT_EMAIL[3];
        Map<UUID, Integer> orderHistoryBefore = orderUseCases.getOrderHistory( clientEmail3 );

        // when
        shoppingBasketUseCases.addProductToShoppingBasket( clientEmail3, thingId1,
                stock1before-2 );
        shoppingBasketUseCases.addProductToShoppingBasket( clientEmail3, thingId2, 4 );
        shoppingBasketUseCases.addProductToShoppingBasket( clientEmail3, thingId3, 5 );
        shoppingBasketUseCases.checkout( clientEmail3 );
        int stock1after = storageUnitUseCases.getAvailableStock( thingId1 );
        int stock2after = storageUnitUseCases.getAvailableStock( thingId2 );
        int stock3after = storageUnitUseCases.getAvailableStock( thingId3 );
        Map<UUID, Integer> orderHistoryAfter = orderUseCases.getOrderHistory( clientEmail3 );

        // then
        assertEquals( 0, orderHistoryBefore.size() );
        assertEquals( 2, stock1after );
        assertEquals( stock2before-4, stock2after );
        assertEquals( stock3before-5, stock3after );
        assertEquals( 3, orderHistoryAfter.size() );
        assertEquals( stock1before-2, orderHistoryAfter.get( thingId1 ) );
        assertEquals( 4, orderHistoryAfter.get( thingId2 ) );
        assertEquals( 5, orderHistoryAfter.get( thingId3 ) );
    }


    @Test
    public void testCheckoutInvalid() {
        // given
        EmailType clientEmail3 = CLIENT_EMAIL[3];
        EmailType clientEmail5 = CLIENT_EMAIL[5];
        UUID thingId2 = (UUID) THING_DATA[2][0];

        // when
        shoppingBasketUseCases.addProductToShoppingBasket( clientEmail5, thingId2, 4 );
        shoppingBasketUseCases.removeProductFromShoppingBasket( clientEmail5, thingId2, 4 );

        // then
        assertThrows( ShopException.class, () -> shoppingBasketUseCases.checkout( nonExistingEmail ) );
        assertThrows( ShopException.class, () -> shoppingBasketUseCases.checkout( clientEmail3 ) );
        assertThrows( ShopException.class, () -> shoppingBasketUseCases.checkout( clientEmail5 ) );
    }


}
