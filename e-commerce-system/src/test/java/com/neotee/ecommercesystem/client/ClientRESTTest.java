package com.neotee.ecommercesystem.client;

import com.neotee.ecommercesystem.FactoryMethodInvoker;
import com.neotee.ecommercesystem.usecases.ClientRegistrationUseCases;
import com.neotee.ecommercesystem.usecases.ClientType;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.HomeAddressType;
import com.neotee.ecommercesystem.usecases.masterdata.ClientMasterDataInitializer;
import com.neotee.ecommercesystem.usecases.masterdata.Purgatory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import javax.transaction.Transactional;
import java.util.UUID;

import static com.neotee.ecommercesystem.ClientTestHelper.CLIENT_NAME;
import static com.neotee.ecommercesystem.usecases.masterdata.ClientMasterDataInitializer.CLIENT_ADDRESS;
import static com.neotee.ecommercesystem.usecases.masterdata.ClientMasterDataInitializer.CLIENT_EMAIL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ClientRESTTest {
    @Autowired
    private ClientRegistrationUseCases clientRegistrationUseCases;
    @Autowired
    private Purgatory purgatory;

    @Autowired
    private MockMvc mockMvc;

    private ClientMasterDataInitializer clientMasterDataInitializer;
    private EmailType nonExistingEmail;
    private UUID randomId;

    @BeforeEach
    public void setUp() {
        purgatory.deleteEverything();

        clientMasterDataInitializer = new ClientMasterDataInitializer( clientRegistrationUseCases );
        clientMasterDataInitializer.registerAllClients();

        nonExistingEmail = FactoryMethodInvoker.instantiateEmail(
                "harry@sally.de" );
        randomId = UUID.randomUUID();
    }


    @Test
    public void testInvalidClientUris() throws Exception {
        // given
        String allClientsUri = "/clients";
        String randomIdUri = "/clients/" +  randomId;
        String nonExistingEmailUri = "/clients?email=" + nonExistingEmail;
        String randomParamUri = "/clients?randomParam=randomValue";

        // when
        // then
        mockMvc.perform( get( allClientsUri ) ).andExpect( status().isMethodNotAllowed() );
        mockMvc.perform( get( randomIdUri ) ).andExpect( status().isNotFound() );
        mockMvc.perform( get( nonExistingEmailUri ) ).andExpect( status().isNotFound() );
        mockMvc.perform( get( randomParamUri ) ).andExpect( status().is4xxClientError() );
    }


    @Test
    public void testRandomClientId() throws Exception {
        // given
        String invalidUri = "/clients/" +  randomId;

        // when
        // then
        mockMvc.perform( get( invalidUri ) ).andExpect( status().isNotFound() );
    }


    @Test
    public void testQueryClient() throws Exception {
        // given
        EmailType email4 = CLIENT_EMAIL[4];
        String clientUri4 = "/clients?email=" + email4;

        // when
        ClientType client4 = clientRegistrationUseCases.getClientData( email4 );
        String name = CLIENT_NAME[4];
        HomeAddressType homeAddress4 = CLIENT_ADDRESS[4];

        // then (check using JSONPath)
        mockMvc.perform( get( clientUri4 ) )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.name" ).value( name ) )
                // id is position 0 (cannot be tested with current API structure), name is position 1,
                // email is 2, home address is 3)
                .andExpect( jsonPath( "$..emailString" ).value( email4.toString() ) )
                .andExpect( jsonPath( "$..city" ).value( homeAddress4.getCity() ) )
                .andExpect( jsonPath( "$..zipCodeString" ).value(
                        homeAddress4.getZipCode().toString() ) );
    }

}
