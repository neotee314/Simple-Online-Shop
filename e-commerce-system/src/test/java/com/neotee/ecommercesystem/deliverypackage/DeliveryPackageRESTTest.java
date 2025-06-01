package com.neotee.ecommercesystem.deliverypackage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neotee.ecommercesystem.ShoppingBasketRESTHelper;
import com.neotee.ecommercesystem.restdtos.StorageUnitIdDTO;
import com.neotee.ecommercesystem.usecases.*;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import com.neotee.ecommercesystem.usecases.masterdata.*;
import com.neotee.ecommercesystem.usecases.masterdata.ThingAndStockMasterDataInitializer;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


import java.util.*;

import static com.neotee.ecommercesystem.ClientTestHelper.CLIENT_EMAIL;
import static com.neotee.ecommercesystem.ThingAndStockTestHelper.STORAGE_UNIT_ID;
import static com.neotee.ecommercesystem.ThingAndStockTestHelper.THING_DATA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DeliveryPackageRESTTest {
    @Autowired
    private ClientRegistrationUseCases clientRegistrationUseCases;
    @Autowired
    private ThingCatalogUseCases thingCatalogUseCases;
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
                mockMvc, thingCatalogUseCases, storageUnitUseCases);
        purgatory.deleteEverything();


        clientMasterDataInitializer = new ClientMasterDataInitializer(clientRegistrationUseCases);
        clientMasterDataInitializer.registerAllClients();

        thingAndStockMasterDataInitializer = new ThingAndStockMasterDataInitializer(
                thingCatalogUseCases, storageUnitUseCases);
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
        // given
        String alldeliveryPackagesUri = "/deliveryPackages";
        String randomIdUri = "/deliveryPackages/" + UUID.randomUUID();
        String randomOrderUri = "/deliveryPackages?orderId=" + UUID.randomUUID();
        String randomParamUri = "/deliveryPackages?randomParam=randomValue";

        // when
        // then
        mockMvc.perform(get(alldeliveryPackagesUri)).andExpect(status().isMethodNotAllowed());
        mockMvc.perform(get(randomIdUri)).andExpect(status().isNotFound());
        mockMvc.perform(get(randomOrderUri)).andExpect(status().isNotFound());
        mockMvc.perform(get(randomParamUri)).andExpect(status().is4xxClientError());
    }


    /**
     * Different clients always buy the same thing (no. 7). The thing is available in
     * storage unit no. {1, 2, 3}. The test checks if the correct (closest) storage unit is
     * selected for each client. For client 0 and 3, there is only one solution. These two
     * are tested here.
     */
    @Test
    public void testClosestDeliveryPackage() throws Exception {
        // given
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

        // when
        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId0, thingId7, 1);
        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId3, thingId7, 1);
        UUID orderId0 = shoppingBasketRESTHelper.checkout(shoppingBasketId0);
        UUID orderId3 = shoppingBasketRESTHelper.checkout(shoppingBasketId3);

        // then
        checkDeliveryPackage(orderId0, storageUnitMap0);
        checkDeliveryPackage(orderId3, storageUnitMap3);
    }



    @Test
    public void testClosestSingleDeliveryPackagesWins() throws Exception {
        // given
        EmailType clientEmail3 = CLIENT_EMAIL[3];
        UUID shoppingBasketId3 = shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail3);
        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId3, (UUID) THING_DATA[8][0], 2);
        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId3, (UUID) THING_DATA[11][0], 2);
        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId3, (UUID) THING_DATA[14][0], 2);
        // that basket could have been served from storage unit 4 or 7, but 4 is closer to the client
        UUID orderId = shoppingBasketRESTHelper.checkout(shoppingBasketId3);

        // when
        Map<UUID, Map<UUID, Integer>> storageUnitMap = Map.of(
                STORAGE_UNIT_ID[4], map8_11_14_quantity_2_2_2
        );


        // then
        checkDeliveryPackage(orderId, storageUnitMap);
    }


    /**
     * Hint: (see comment on the first test)
     */

    @Test
    public void testStorageUnitWithEnoughCapacityWins() throws Exception {
        // given
        EmailType clientEmail3 = CLIENT_EMAIL[3];
        UUID shoppingBasketId3 = shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail3);

        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId3, (UUID) THING_DATA[8][0], 3);
        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId3, (UUID) THING_DATA[11][0], 3);
        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId3, (UUID) THING_DATA[14][0], 4);
        // that basket could have been served from storage unit 4 or 7, 4 is closer to the client,
        // but only 7 has enough capacity
        UUID orderId = shoppingBasketRESTHelper.checkout(shoppingBasketId3);

        // when
        Map<UUID, Map<UUID, Integer>> storageUnitMap = Map.of(
                STORAGE_UNIT_ID[7], map8_11_14_quantity_3_3_4
        );

        // then
        checkDeliveryPackage(orderId, storageUnitMap);
    }


    /**
     * Hint: (see comment on the first test)
     */
    @Test
    public void testTwoDeliveryPackages() throws Exception {
        // given
        EmailType clientEmail6 = CLIENT_EMAIL[6];
        UUID shoppingBasketId6 = shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail6);

        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId6, (UUID) THING_DATA[10][0], 1);
        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId6, (UUID) THING_DATA[11][0], 1);
        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId6, (UUID) THING_DATA[12][0], 1);
        // that basket needs two delivery packages (2 + 1 things) anyway, and there
        // are 2 options for the bigger one: 10+12 from 5, or 10+11 from 7. The first one is
        // closer. The smaller can then be served from 4 (closest), 7, or 8.
        UUID orderId = shoppingBasketRESTHelper.checkout(shoppingBasketId6);

        // when
        Map<UUID, Map<UUID, Integer>> storageUnitMap = Map.of(
                STORAGE_UNIT_ID[5], map10_12_quantity_1_1,
                STORAGE_UNIT_ID[4], map11_quantity_1
        );

        // then
        checkDeliveryPackage(orderId, storageUnitMap);
    }


    /**
     * Hint: (see comment on the first test)
     */
    @Test
    public void testTwoBigDeliveryPackages() throws Exception {
        // given
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

        // when
        Map<UUID, Map<UUID, Integer>> storageUnitMap = Map.of(
                STORAGE_UNIT_ID[7], map8_9_10_11_quantity_2_1_4_2,
                STORAGE_UNIT_ID[5], map12_quantity_10
        );

        // then
        checkDeliveryPackage(orderId, storageUnitMap);
    }


    /**
     * Hint: (see comment on the first test)
     */
    @Test
    public void testOnlyOneSolution() throws Exception {
        // given
        EmailType clientEmail6 = CLIENT_EMAIL[6];
        UUID shoppingBasketId3 = shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail6);

        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId3, (UUID) THING_DATA[12][0], 10);
        UUID orderId = shoppingBasketRESTHelper.checkout(shoppingBasketId3);

        // when
        Map<UUID, Map<UUID, Integer>> storageUnitMap = Map.of(
                STORAGE_UNIT_ID[5], map12_quantity_10
        );

        // then
        checkDeliveryPackage(orderId, storageUnitMap);
    }


    /**
     * Calls the /deliveryPackages endpoint and checks the response.
     *
     * @param orderId        The id of the order.
     * @param storageUnitMap The expected storage units and their quantities, as a map of maps:
     *                       storage units -> ( things -> quantity )
     * @throws Exception
     */
    public void checkDeliveryPackage(UUID orderId,
                                     Map<UUID, Map<UUID, Integer>> storageUnitMap) throws Exception {
        // call the GET endpoint
        String deliveryPackageUri = "/deliveryPackages?orderId=" + orderId.toString();
        ResultActions resultActions = mockMvc.perform(get(deliveryPackageUri))
                .andExpect(status().isOk());

        // check the response
        ObjectMapper objectMapper = new ObjectMapper();
        resultActions.andExpect(jsonPath("$.length()").value(storageUnitMap.size()));
        for (int i = 0; i < storageUnitMap.size(); i++) {
            resultActions.andExpect(jsonPath("$[" + i + "].id").exists());
        }
        StorageUnitIdDTO[] storageUnitIdDTOs = objectMapper.readValue(
                resultActions.andReturn().getResponse().getContentAsString(), StorageUnitIdDTO[].class);
        int i = 0;
        for (StorageUnitIdDTO storageUnitIdDTO : storageUnitIdDTOs) {
            UUID storageUnitId = storageUnitIdDTO.getStorageUnitId();
            Map<UUID, Integer> quantityMap = storageUnitMap.get(storageUnitId);
            int numOfThings = quantityMap.size();
            String jsonPathLengthExpr = "$[" + i + "].deliveryPackageParts.length()";
            resultActions.andExpect(jsonPath(jsonPathLengthExpr).value(numOfThings));
            for (Map.Entry<UUID, Integer> entry : quantityMap.entrySet()) {
                UUID thingId = entry.getKey();
                Integer quantity = entry.getValue();
                resultActions.andExpect(jsonPath("$[" + i + "].deliveryPackageParts[?(@.thingId == '"
                        + thingId + "')].quantity").value(quantity));
            }
            i++;
        }
    }
}
