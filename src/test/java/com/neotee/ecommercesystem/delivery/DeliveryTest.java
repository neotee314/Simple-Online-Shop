package com.neotee.ecommercesystem.delivery;

import com.neotee.ecommercesystem.config.TestContainersConfiguration;
import com.neotee.ecommercesystem.exceptions.ShopException;
import com.neotee.ecommercesystem.helper.ClientMasterDataInitializer;
import com.neotee.ecommercesystem.helper.ThingAndStockMasterDataInitializer;
import com.neotee.ecommercesystem.usecases.*;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import com.neotee.ecommercesystem.usecases.masterdata.Purgatory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

import static com.neotee.ecommercesystem.helper.ClientMasterDataInitializer.CLIENT_EMAIL;
import static com.neotee.ecommercesystem.helper.ThingAndStockMasterDataInitializer.THING_DATA;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Import(TestContainersConfiguration.class)
public class DeliveryTest {

    @Autowired
    private DeliveryUseCases deliveryUseCases;

    @Autowired
    private ShoppingBasketUseCases shoppingBasketUseCases;

    @Autowired
    private ClientRegistrationUseCases clientRegistrationUseCases;

    @Autowired
    private ProductCatalogUseCases productCatalogUseCases;

    @Autowired
    private StorageUnitUseCases storageUnitUseCases;

    @Autowired
    private Purgatory purgatory;

    private ClientMasterDataInitializer clientMasterDataInitializer;
    private ThingAndStockMasterDataInitializer thingAndStockMasterDataInitializer;

    @BeforeEach
    public void setUp() {
        purgatory.deleteEverything();

        clientMasterDataInitializer =
                new ClientMasterDataInitializer(clientRegistrationUseCases);
        clientMasterDataInitializer.registerAllClients();

        thingAndStockMasterDataInitializer =
                new ThingAndStockMasterDataInitializer(
                        productCatalogUseCases,
                        storageUnitUseCases
                );

        thingAndStockMasterDataInitializer.addAllThings();
        thingAndStockMasterDataInitializer.addAllStorageUnits();
        thingAndStockMasterDataInitializer.addAllStock();
    }

    @Test
    public void testTriggerDelivery() {
        EmailType clientEmail = CLIENT_EMAIL[3];
        UUID thingId = (UUID) THING_DATA[8][0];

        shoppingBasketUseCases.addProductToShoppingBasket(
                clientEmail,
                thingId,
                2
        );

        UUID orderId = shoppingBasketUseCases.checkout(clientEmail);

        ClientType client = clientRegistrationUseCases.getClientData(clientEmail);

        UUID deliveryId = deliveryUseCases.triggerDelivery(
                orderId,
                client
        );

        assertNotNull(deliveryId);
    }

    @Test
    public void testTriggerDeliveryCreatesDeliveryPackages() {
        EmailType clientEmail = CLIENT_EMAIL[3];
        UUID thingId = (UUID) THING_DATA[8][0];

        shoppingBasketUseCases.addProductToShoppingBasket(
                clientEmail,
                thingId,
                2
        );

        UUID orderId = shoppingBasketUseCases.checkout(clientEmail);

        ClientType client = clientRegistrationUseCases.getClientData(clientEmail);

        UUID deliveryId = deliveryUseCases.triggerDelivery(
                orderId,
                client
        );

        List<UUID> packageIds =
                deliveryUseCases.getDeliveryPackages(deliveryId);

        assertFalse(packageIds.isEmpty());
    }

    @Test
    public void testNewDeliveryPackagesHaveNotShippedStatus() {
        EmailType clientEmail = CLIENT_EMAIL[3];
        UUID thingId = (UUID) THING_DATA[8][0];

        shoppingBasketUseCases.addProductToShoppingBasket(
                clientEmail,
                thingId,
                2
        );

        UUID orderId = shoppingBasketUseCases.checkout(clientEmail);

        ClientType client = clientRegistrationUseCases.getClientData(clientEmail);

        UUID deliveryId = deliveryUseCases.triggerDelivery(
                orderId,
                client
        );

        List<UUID> packageIds =
                deliveryUseCases.getDeliveryPackages(deliveryId);

        assertFalse(packageIds.isEmpty());

        packageIds.forEach(packageId ->
                assertEquals(
                        "NOT_SHIPPED",
                        deliveryUseCases.getDeliveryPackageStatus(packageId)
                )
        );
    }

    @Test
    public void testUpdateDeliveryPackageStatus() {
        EmailType clientEmail = CLIENT_EMAIL[3];
        UUID thingId = (UUID) THING_DATA[8][0];

        shoppingBasketUseCases.addProductToShoppingBasket(
                clientEmail,
                thingId,
                2
        );

        UUID orderId = shoppingBasketUseCases.checkout(clientEmail);

        ClientType client = clientRegistrationUseCases.getClientData(clientEmail);

        UUID deliveryId = deliveryUseCases.triggerDelivery(
                orderId,
                client
        );

        UUID packageId =
                deliveryUseCases.getDeliveryPackages(deliveryId).get(0);

        deliveryUseCases.updateDeliveryPackageStatus(
                packageId,
                "IN_TRANSIT"
        );

        assertEquals(
                "IN_TRANSIT",
                deliveryUseCases.getDeliveryPackageStatus(packageId)
        );

        deliveryUseCases.updateDeliveryPackageStatus(
                packageId,
                "DELIVERED"
        );

        assertEquals(
                "DELIVERED",
                deliveryUseCases.getDeliveryPackageStatus(packageId)
        );
    }

    @Test
    public void testDeliveryHistory() {
        EmailType clientEmail = CLIENT_EMAIL[3];
        UUID thingId = (UUID) THING_DATA[8][0];

        shoppingBasketUseCases.addProductToShoppingBasket(
                clientEmail,
                thingId,
                2
        );

        UUID orderId = shoppingBasketUseCases.checkout(clientEmail);

        ClientType client = clientRegistrationUseCases.getClientData(clientEmail);

        UUID deliveryId = deliveryUseCases.triggerDelivery(
                orderId,
                client
        );

        List<UUID> history =
                deliveryUseCases.getDeliveryHistory(clientEmail);

        assertTrue(history.contains(deliveryId));
    }

    @Test
    public void testDeliveryHistoryOnlyContainsClientsDeliveries() {
        EmailType clientEmail1 = CLIENT_EMAIL[3];
        EmailType clientEmail2 = CLIENT_EMAIL[6];
        UUID thingId = (UUID) THING_DATA[8][0];

        shoppingBasketUseCases.addProductToShoppingBasket(
                clientEmail1,
                thingId,
                1
        );

        UUID orderId = shoppingBasketUseCases.checkout(clientEmail1);

        ClientType client = clientRegistrationUseCases.getClientData(clientEmail1);

        UUID deliveryId = deliveryUseCases.triggerDelivery(
                orderId,
                client
        );

        List<UUID> history1 =
                deliveryUseCases.getDeliveryHistory(clientEmail1);

        List<UUID> history2 =
                deliveryUseCases.getDeliveryHistory(clientEmail2);

        assertTrue(history1.contains(deliveryId));
        assertFalse(history2.contains(deliveryId));
    }

    @Test
    public void testGetDeliveryPackagesForNonExistingDelivery() {
        UUID deliveryId = UUID.randomUUID();

        assertThrows(
                ShopException.class,
                () -> deliveryUseCases.getDeliveryPackages(deliveryId)
        );
    }

    @Test
    public void testGetDeliveryPackageStatusForNonExistingPackage() {
        UUID packageId = UUID.randomUUID();

        assertThrows(
                ShopException.class,
                () -> deliveryUseCases.getDeliveryPackageStatus(packageId)
        );
    }

    @Test
    public void testUpdateNonExistingDeliveryPackage() {
        UUID packageId = UUID.randomUUID();

        assertThrows(
                ShopException.class,
                () -> deliveryUseCases.updateDeliveryPackageStatus(
                        packageId,
                        "IN_TRANSIT"
                )
        );
    }

    @Test
    public void testNullDeliveryId() {
        assertThrows(
                ShopException.class,
                () -> deliveryUseCases.getDeliveryPackages(null)
        );
    }

    @Test
    public void testNullDeliveryPackageId() {
        assertThrows(
                ShopException.class,
                () -> deliveryUseCases.getDeliveryPackageStatus(null)
        );
    }

    @Test
    public void testNullStatus() {
        UUID packageId = UUID.randomUUID();

        assertThrows(
                ShopException.class,
                () -> deliveryUseCases.updateDeliveryPackageStatus(
                        packageId,
                        null
                )
        );
    }

    @Test
    public void testNullDeliveryRecipient() {
        EmailType clientEmail = CLIENT_EMAIL[3];
        UUID thingId = (UUID) THING_DATA[8][0];

        shoppingBasketUseCases.addProductToShoppingBasket(
                clientEmail,
                thingId,
                1
        );

        UUID orderId = shoppingBasketUseCases.checkout(clientEmail);

        assertThrows(
                ShopException.class,
                () -> deliveryUseCases.triggerDelivery(
                        orderId,
                        null
                )
        );
    }

    @Test
    public void testNonExistingOrder() {
        EmailType clientEmail = CLIENT_EMAIL[3];
        ClientType client = clientRegistrationUseCases.getClientData(clientEmail);

        assertThrows(
                ShopException.class,
                () -> deliveryUseCases.triggerDelivery(
                        UUID.randomUUID(),
                        client
                )
        );
    }

    @Test
    public void testNullOrderId() {
        EmailType clientEmail = CLIENT_EMAIL[3];
        ClientType client = clientRegistrationUseCases.getClientData(clientEmail);

        assertThrows(
                ShopException.class,
                () -> deliveryUseCases.triggerDelivery(
                        null,
                        client
                )
        );
    }

    @Test
    public void testDeleteAllDeliveries() {
        EmailType clientEmail = CLIENT_EMAIL[3];
        UUID thingId = (UUID) THING_DATA[8][0];

        shoppingBasketUseCases.addProductToShoppingBasket(
                clientEmail,
                thingId,
                2
        );

        UUID orderId = shoppingBasketUseCases.checkout(clientEmail);

        ClientType client = clientRegistrationUseCases.getClientData(clientEmail);

        UUID deliveryId = deliveryUseCases.triggerDelivery(
                orderId,
                client
        );

        assertNotNull(deliveryId);

        assertFalse(
                deliveryUseCases.getDeliveryHistory(clientEmail).isEmpty()
        );

        deliveryUseCases.deleteAllDeliveries();

        assertTrue(
                deliveryUseCases.getDeliveryHistory(clientEmail).isEmpty()
        );
    }
}