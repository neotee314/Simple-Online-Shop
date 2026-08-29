package com.neotee.ecommercesystem.deliverypackage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neotee.ecommercesystem.config.TestContainersConfiguration;
import com.neotee.ecommercesystem.helper.ShoppingBasketRESTHelper;
import com.neotee.ecommercesystem.restdtos.*;
import com.neotee.ecommercesystem.usecases.ClientRegistrationUseCases;
import com.neotee.ecommercesystem.usecases.ProductCatalogUseCases;
import com.neotee.ecommercesystem.usecases.StorageUnitUseCases;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import com.neotee.ecommercesystem.helper.ClientMasterDataInitializer;
import com.neotee.ecommercesystem.usecases.masterdata.Purgatory;
import com.neotee.ecommercesystem.helper.ThingAndStockMasterDataInitializer;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.*;

import static com.neotee.ecommercesystem.helper.ClientMasterDataInitializer.CLIENT_EMAIL;
import static com.neotee.ecommercesystem.helper.ThingAndStockMasterDataInitializer.STORAGE_UNIT_ID;
import static com.neotee.ecommercesystem.helper.ThingAndStockMasterDataInitializer.THING_DATA;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Import(TestContainersConfiguration.class)
public class DeliveryPackageRESTTest {
    @Autowired
    private ClientRegistrationUseCases clientRegistrationUseCases;
    @Autowired
    private ProductCatalogUseCases productCatalogUseCases;
    @Autowired
    private StorageUnitUseCases storageUnitUseCases;
    @Autowired
    private Purgatory purgatory;
    @Autowired
    private MockMvc mockMvc;

    private ShoppingBasketRESTHelper shoppingBasketRESTHelper;
    private Map<UUID, Integer> map8_11_14_quantity_2_2_2, map8_11_14_quantity_3_3_4,
            map10_12_quantity_1_1, map11_quantity_1, map12_quantity_10,
            map8_9_10_11_quantity_2_1_4_2;

    private ClientMasterDataInitializer clientMasterDataInitializer;
    private ThingAndStockMasterDataInitializer thingAndStockMasterDataInitializer;

    @BeforeEach
    public void setUp() {
        shoppingBasketRESTHelper = new ShoppingBasketRESTHelper(
                mockMvc, productCatalogUseCases, storageUnitUseCases);
        purgatory.deleteEverything();

        clientMasterDataInitializer = new ClientMasterDataInitializer(clientRegistrationUseCases);
        clientMasterDataInitializer.registerAllClients();

        thingAndStockMasterDataInitializer = new ThingAndStockMasterDataInitializer(
                productCatalogUseCases, storageUnitUseCases);
        thingAndStockMasterDataInitializer.addAllThings();
        thingAndStockMasterDataInitializer.addAllStorageUnits();
        thingAndStockMasterDataInitializer.addAllStock();

        map8_11_14_quantity_2_2_2 = new HashMap<>() {{
            put((UUID) THING_DATA[8][0], 2);
            put((UUID) THING_DATA[11][0], 2);
            put((UUID) THING_DATA[14][0], 2);
        }};
        map8_11_14_quantity_3_3_4 = new HashMap<>() {{
            put((UUID) THING_DATA[8][0], 3);
            put((UUID) THING_DATA[11][0], 3);
            put((UUID) THING_DATA[14][0], 4);
        }};
        map10_12_quantity_1_1 = new HashMap<>() {{
            put((UUID) THING_DATA[10][0], 1);
            put((UUID) THING_DATA[12][0], 1);
        }};
        map11_quantity_1 = new HashMap<>() {{
            put((UUID) THING_DATA[11][0], 1);
        }};
        map12_quantity_10 = new HashMap<>() {{
            put((UUID) THING_DATA[12][0], 10);
        }};
        map8_9_10_11_quantity_2_1_4_2 = new HashMap<>() {{
            put((UUID) THING_DATA[8][0], 2);
            put((UUID) THING_DATA[9][0], 1);
            put((UUID) THING_DATA[10][0], 4);
            put((UUID) THING_DATA[11][0], 2);
        }};
    }

    @Test
    public void testInvalidUris() throws Exception {
        String alldeliveryPackagesUri = "/api/v1/deliveryPackages";
        String randomIdUri = "/api/v1/deliveryPackages/" + UUID.randomUUID();
        String randomOrderUri = "/api/v1/deliveryPackages?orderId=" + UUID.randomUUID();
        String randomParamUri = "/api/v1/deliveryPackages?randomParam=randomValue";

        mockMvc.perform(get(alldeliveryPackagesUri)).andExpect(status().isMethodNotAllowed());
        mockMvc.perform(get(randomIdUri)).andExpect(status().isNotFound());
        mockMvc.perform(get(randomOrderUri)).andExpect(status().isNotFound());
        mockMvc.perform(get(randomParamUri)).andExpect(status().is4xxClientError());
    }

    @Test
    public void testClosestDeliveryPackage() throws Exception {
        UUID thingId7 = (UUID) THING_DATA[7][0];
        EmailType clientEmail0 = CLIENT_EMAIL[0];
        UUID shoppingBasketId0 = shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail0);
        EmailType clientEmail3 = CLIENT_EMAIL[3];
        UUID shoppingBasketId3 = shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail3);
        Map<UUID, Map<UUID, Integer>> storageUnitMap0 = Map.of(
                STORAGE_UNIT_ID[2], Map.of(thingId7, 1)
        );
        Map<UUID, Map<UUID, Integer>> storageUnitMap3 = Map.of(
                STORAGE_UNIT_ID[3], Map.of(thingId7, 1)
        );

        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId0, thingId7, 1);
        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId3, thingId7, 1);
        UUID orderId0 = shoppingBasketRESTHelper.checkout(shoppingBasketId0);
        UUID orderId3 = shoppingBasketRESTHelper.checkout(shoppingBasketId3);

        checkDeliveryPackage(orderId0, storageUnitMap0);
        checkDeliveryPackage(orderId3, storageUnitMap3);
    }

    @Test
    public void testClosestSingleDeliveryPackagesWins() throws Exception {
        EmailType clientEmail3 = CLIENT_EMAIL[3];
        UUID shoppingBasketId3 = shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail3);
        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId3, (UUID) THING_DATA[8][0], 2);
        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId3, (UUID) THING_DATA[11][0], 2);
        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId3, (UUID) THING_DATA[14][0], 2);
        UUID orderId = shoppingBasketRESTHelper.checkout(shoppingBasketId3);

        Map<UUID, Map<UUID, Integer>> storageUnitMap = Map.of(
                STORAGE_UNIT_ID[4], map8_11_14_quantity_2_2_2
        );

        checkDeliveryPackage(orderId, storageUnitMap);
    }

    @Test
    public void testStorageUnitWithEnoughCapacityWins() throws Exception {
        EmailType clientEmail3 = CLIENT_EMAIL[3];
        UUID shoppingBasketId3 = shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail3);

        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId3, (UUID) THING_DATA[8][0], 3);
        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId3, (UUID) THING_DATA[11][0], 3);
        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId3, (UUID) THING_DATA[14][0], 4);

        UUID orderId = shoppingBasketRESTHelper.checkout(shoppingBasketId3);

        Map<UUID, Map<UUID, Integer>> storageUnitMap = Map.of(
                STORAGE_UNIT_ID[7], map8_11_14_quantity_3_3_4
        );

        checkDeliveryPackage(orderId, storageUnitMap);
    }

    @Test
    public void testTwoDeliveryPackages() throws Exception {
        EmailType clientEmail6 = CLIENT_EMAIL[6];
        UUID shoppingBasketId6 = shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail6);

        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId6, (UUID) THING_DATA[10][0], 1);
        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId6, (UUID) THING_DATA[11][0], 1);
        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId6, (UUID) THING_DATA[12][0], 1);
        UUID orderId = shoppingBasketRESTHelper.checkout(shoppingBasketId6);

        Map<UUID, Map<UUID, Integer>> storageUnitMap = Map.of(STORAGE_UNIT_ID[5], map10_12_quantity_1_1, STORAGE_UNIT_ID[4], map11_quantity_1);

        checkDeliveryPackage(orderId, storageUnitMap);
    }

    @Test
    public void testTwoBigDeliveryPackages() throws Exception {
        EmailType clientEmail2 = CLIENT_EMAIL[2];
        UUID shoppingBasketId2 = shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail2);

        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId2, (UUID) THING_DATA[8][0], 2);
        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId2, (UUID) THING_DATA[9][0], 1);
        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId2, (UUID) THING_DATA[10][0], 4);
        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId2, (UUID) THING_DATA[11][0], 2);
        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId2, (UUID) THING_DATA[12][0], 10);
        UUID orderId = shoppingBasketRESTHelper.checkout(shoppingBasketId2);

        Map<UUID, Map<UUID, Integer>> storageUnitMap = Map.of(
                STORAGE_UNIT_ID[7], map8_9_10_11_quantity_2_1_4_2,
                STORAGE_UNIT_ID[5], map12_quantity_10
        );

        checkDeliveryPackage(orderId, storageUnitMap);
    }

    @Test
    public void testOnlyOneSolution() throws Exception {
        EmailType clientEmail6 = CLIENT_EMAIL[6];
        UUID shoppingBasketId3 = shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail6);

        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId3, (UUID) THING_DATA[12][0], 10);
        UUID orderId = shoppingBasketRESTHelper.checkout(shoppingBasketId3);

        Map<UUID, Map<UUID, Integer>> storageUnitMap = Map.of(
                STORAGE_UNIT_ID[5], map12_quantity_10
        );

        checkDeliveryPackage(orderId, storageUnitMap);
    }

    public void checkDeliveryPackage(UUID orderId, Map<UUID, Map<UUID, Integer>> storageUnitMap) throws Exception {
        String deliveryPackageUri = "/api/v1/deliveryPackages?orderId=" + orderId;
        ResultActions resultActions = mockMvc.perform(get(deliveryPackageUri))
                .andExpect(status().isOk());

        ObjectMapper objectMapper = new ObjectMapper();
        String json = resultActions.andReturn().getResponse().getContentAsString();
        DeliveryPackageResponseDTO[] response = objectMapper.readValue(json, DeliveryPackageResponseDTO[].class);

        assertEquals(storageUnitMap.size(), response.length);

        for (DeliveryPackageResponseDTO dto : response) {
            UUID storageUnitId = dto.storageUnitId();
            Map<UUID, Integer> expectedMap = storageUnitMap.get(storageUnitId);
            assertNotNull(expectedMap);

            List<DeliveryPackagePartResponseDTO> parts = dto.parts();
            assertEquals(expectedMap.size(), parts.size());

            for (Map.Entry<UUID, Integer> entry : expectedMap.entrySet()) {
                UUID thingId = entry.getKey();
                Integer expectedQuantity = entry.getValue();

                boolean found = false;
                for (DeliveryPackagePartResponseDTO part : parts) {
                    UUID actualThingId = part.productId();
                    if (actualThingId.equals(thingId)) {
                        assertEquals(expectedQuantity, part.quantity());
                        found = true;
                        break;
                    }
                }
                assertTrue(found);
            }
        }
    }
}