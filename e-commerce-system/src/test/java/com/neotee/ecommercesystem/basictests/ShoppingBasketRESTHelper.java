package com.neotee.ecommercesystem.basictests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.neotee.ecommercesystem.ThingAndStockMasterDataInitializer;
import com.neotee.ecommercesystem.shopsystem.client.application.dto.ClientResponseDto;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto.CheckoutResponseDTO;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto.ShoppingBasketPartRequestDTO;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto.ShoppingBasketResponseDTO;
import com.neotee.ecommercesystem.usecases.ProductCatalogUseCases;
import com.neotee.ecommercesystem.usecases.StorageUnitUseCases;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;


import java.util.Map;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ShoppingBasketRESTHelper {
    private MockMvc mockMvc;
    private ProductCatalogUseCases productCatalogUseCases;
    private StorageUnitUseCases storageUnitUseCases;

    private static final ResultMatcher CREATED = status().isCreated();
    private static final ResultMatcher OK = status().isOk();
    private ThingAndStockMasterDataInitializer thingAndStockMasterDataInitializer;

    public ShoppingBasketRESTHelper(MockMvc mockMvc,
                                    ProductCatalogUseCases productCatalogUseCases,
                                    StorageUnitUseCases storageUnitUseCases) {
        this.mockMvc = mockMvc;
        this.productCatalogUseCases = productCatalogUseCases;
        this.storageUnitUseCases = storageUnitUseCases;
        thingAndStockMasterDataInitializer = new ThingAndStockMasterDataInitializer(
                productCatalogUseCases, storageUnitUseCases);
    }

    public UUID getQueryShoppingBasket(EmailType email) throws Exception {
        return getQueryShoppingBasket(email, null);
    }

    public UUID getQueryShoppingBasket(EmailType email, Map<UUID, Integer> quantityMap)
            throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        // Step 1: Get client by email
        String clientUri = "/api/v1/clients?email=" + email.toString();
        MvcResult clientGetResult = mockMvc.perform(get(clientUri))
                .andExpect(status().isOk())
                .andReturn();


        ClientResponseDto clientResponse = objectMapper.readValue(
                clientGetResult.getResponse().getContentAsString(),
                ClientResponseDto.class
        );

        UUID clientId = clientResponse.clientId().getId();

        // Step 2: Get shopping basket by clientId
        String shoppingBasketUri = "/api/v1/shoppingBaskets?clientId=" + clientId.toString();
        MvcResult basketResult = mockMvc.perform(get(shoppingBasketUri))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingBasketResponseDTO basketResponse = objectMapper.readValue(
                basketResult.getResponse().getContentAsString(),
                ShoppingBasketResponseDTO.class
        );

        UUID shoppingBasketId = basketResponse.id().getId();

        // Additional checks
        if (quantityMap != null) {
            Float totalSalesPrice = thingAndStockMasterDataInitializer
                    .getTotalSalesPrice(quantityMap);

            ResultActions resultActions = mockMvc.perform(get(shoppingBasketUri))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalPrice").value(totalSalesPrice));

            checkQuantityMap(quantityMap, resultActions);
        }

        return shoppingBasketId;
    }

    public void checkQuantityMap(Map<UUID, Integer> quantityMap, ResultActions resultActions) throws Exception {
        int numOfThings = quantityMap.size();
        resultActions.andExpect(jsonPath("$.parts.length()").value(numOfThings));
        for (Map.Entry<UUID, Integer> entry : quantityMap.entrySet()) {
            UUID thingId = entry.getKey();
            Integer quantity = entry.getValue();
            resultActions.andExpect(jsonPath("$.parts.[?(@.productId.id == '" + thingId + "')].quantity")
                    .value(quantity));
        }
    }

    public void addThingToShoppingBasket(UUID shoppingBasketId, UUID thingId, Integer quantity)
            throws Exception {
        addThingToShoppingBasket(shoppingBasketId, thingId, quantity, null);
    }

    public void addThingToShoppingBasket(UUID shoppingBasketId, UUID thingId, Integer quantity,
                                         ResultMatcher expectedStatus) throws Exception {
        ResultMatcher status = expectedStatus == null ? CREATED : expectedStatus;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        ShoppingBasketPartRequestDTO dto = new ShoppingBasketPartRequestDTO(thingId, quantity);
        String quantityJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/shoppingBaskets/" + shoppingBasketId + "/parts")
                        .contentType(APPLICATION_JSON).content(quantityJson))
                .andExpect(status);
    }

    public void deleteThingFromShoppingBasket(UUID shoppingBasketId, UUID thingId)
            throws Exception {
        deleteThingFromShoppingBasket(shoppingBasketId, thingId, null);
    }

    public void deleteThingFromShoppingBasket(UUID shoppingBasketId, UUID thingId,
                                              ResultMatcher expectedStatus) throws Exception {
        ResultMatcher status = expectedStatus == null ? OK : expectedStatus;
        String uri = "/api/v1/shoppingBaskets/" + shoppingBasketId + "/parts/" + thingId;
        mockMvc.perform(delete(uri)).andExpect(status);
    }

    public UUID checkout(UUID shoppingBasketId) throws Exception {
        return checkout(shoppingBasketId, null);
    }

    public UUID checkout(UUID shoppingBasketId, ResultMatcher expectedStatus) throws Exception {
        ResultMatcher status = expectedStatus == null ? CREATED : expectedStatus;
        String uri = "/api/v1/shoppingBaskets/" + shoppingBasketId + "/checkout";
        MvcResult result = mockMvc.perform(post(uri)).andExpect(status).andReturn();

        if (!status.equals(CREATED)) return null;

        ObjectMapper objectMapper = new ObjectMapper();
        CheckoutResponseDTO checkoutResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CheckoutResponseDTO.class
        );
        return checkoutResponse.orderId().getId();
    }
}