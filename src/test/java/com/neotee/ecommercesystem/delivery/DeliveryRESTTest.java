package com.neotee.ecommercesystem.delivery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neotee.ecommercesystem.config.TestContainersConfiguration;
import com.neotee.ecommercesystem.helper.ClientMasterDataInitializer;
import com.neotee.ecommercesystem.helper.ThingAndStockMasterDataInitializer;
import com.neotee.ecommercesystem.restdtos.UpdateDeliveryPackageStatusRequestDTO;
import com.neotee.ecommercesystem.usecases.*;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import com.neotee.ecommercesystem.usecases.masterdata.Purgatory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.neotee.ecommercesystem.helper.ClientMasterDataInitializer.CLIENT_EMAIL;
import static com.neotee.ecommercesystem.helper.ThingAndStockMasterDataInitializer.THING_DATA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Import(TestContainersConfiguration.class)
public class DeliveryRESTTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    private EmailType clientEmail;
    private UUID deliveryId;
    private UUID packageId;

    @BeforeEach
    public void setUp() {
        purgatory.deleteEverything();

        var clientMasterDataInitializer = new ClientMasterDataInitializer(clientRegistrationUseCases);
        clientMasterDataInitializer.registerAllClients();

        var thingAndStockMasterDataInitializer = new ThingAndStockMasterDataInitializer(
                productCatalogUseCases,
                storageUnitUseCases
        );
        thingAndStockMasterDataInitializer.addAllThings();
        thingAndStockMasterDataInitializer.addAllStorageUnits();
        thingAndStockMasterDataInitializer.addAllStock();

        clientEmail = CLIENT_EMAIL[3];
        UUID thingId = (UUID) THING_DATA[8][0];

        shoppingBasketUseCases.addProductToShoppingBasket(clientEmail, thingId, 2);
        UUID orderId = shoppingBasketUseCases.checkout(clientEmail);

        ClientType client = clientRegistrationUseCases.getClientData(clientEmail);
        deliveryId = deliveryUseCases.triggerDelivery(orderId, client);
        packageId = deliveryUseCases.getDeliveryPackages(deliveryId).get(0);
    }

    @Test
    public void testGetDeliveryById() throws Exception {
        mockMvc.perform(get("/api/v1/deliveries/{deliveryId}", deliveryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(deliveryId.toString()))
                .andExpect(jsonPath("$.orderId").exists())
                .andExpect(jsonPath("$.clientEmail").value(clientEmail.toString()));
    }

    @Test
    public void testGetDeliveryByIdNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        mockMvc.perform(get("/api/v1/deliveries/{deliveryId}", randomId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetDeliveryPackages() throws Exception {
        mockMvc.perform(get("/api/v1/deliveries/{deliveryId}/packages", deliveryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(packageId.toString()))
                .andExpect(jsonPath("$[0].status").value("NOT_SHIPPED"));
    }

    @Test
    public void testGetDeliveryPackagesNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        mockMvc.perform(get("/api/v1/deliveries/{deliveryId}/packages", randomId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetDeliveryPackageStatus() throws Exception {
        mockMvc.perform(get("/api/v1/deliveries/packages/{packageId}/status", packageId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("NOT_SHIPPED"));
    }

    @Test
    public void testGetDeliveryPackageStatusNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        mockMvc.perform(get("/api/v1/deliveries/packages/{packageId}/status", randomId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateDeliveryPackageStatus() throws Exception {
        var request = new UpdateDeliveryPackageStatusRequestDTO("IN_TRANSIT");
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(patch("/api/v1/deliveries/packages/{packageId}/status", packageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/deliveries/packages/{packageId}/status", packageId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("IN_TRANSIT"));
    }

    @Test
    public void testUpdateDeliveryPackageStatusInvalidValue() throws Exception {
        var request = new UpdateDeliveryPackageStatusRequestDTO("INVALID_STATUS");
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(patch("/api/v1/deliveries/packages/{packageId}/status", packageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    public void testUpdateDeliveryPackageStatusNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        var request = new UpdateDeliveryPackageStatusRequestDTO("IN_TRANSIT");
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(patch("/api/v1/deliveries/packages/{packageId}/status", randomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateDeliveryPackageStatusNullStatus() throws Exception {
        String jsonRequest = "{\"status\": null}";

        mockMvc.perform(patch("/api/v1/deliveries/packages/{packageId}/status", packageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    public void testGetDeliveryHistory() throws Exception {
        mockMvc.perform(get("/api/v1/deliveries/history")
                        .param("email", clientEmail.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value(deliveryId.toString()));
    }

    @Test
    public void testGetDeliveryHistoryNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/deliveries/history")
                        .param("email", "nonexistent@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testDeleteAllDeliveries() throws Exception {
        mockMvc.perform(delete("/api/v1/deliveries"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/deliveries/history")
                        .param("email", clientEmail.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}