package com.neotee.ecommercesystem.regression;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.usecases.*;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.*;
import com.neotee.ecommercesystem.usecases.masterdata.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.neotee.ecommercesystem.usecases.masterdata.ClientMasterDataInitializer.*;
import static com.neotee.ecommercesystem.usecases.masterdata.ClientMasterDataInitializer.CLIENT_EMAIL;
import static com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateEmail;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ClientRegistrationTest {
    @Autowired
    private ClientRegistrationUseCases clientRegistrationUseCases;
    @Autowired
    private Purgatory purgatory;

    private EmailType nonExistingEmail;
    private ClientMasterDataInitializer clientMasterDataInitializer;

    @BeforeEach
    public void setUp() {
        purgatory.deleteEverything();

        clientMasterDataInitializer = new ClientMasterDataInitializer( clientRegistrationUseCases );
        nonExistingEmail = instantiateEmail( "this@nononono.de" );
    }



    @Test
    public void testRegisterClientWithDuplicateEmail() {
        // given
        clientMasterDataInitializer.registerAllClients();

        // when
        // then
        assertThrows( ShopException.class, () ->
                clientRegistrationUseCases.register( "Gandalf The Grey", CLIENT_EMAIL[5],
                        CLIENT_ADDRESS[5] ) );
    }

    @Test
    public void testRegisterClientWithDuplicateNameOrHomeAddress() {
        // given
        clientMasterDataInitializer.registerAllClients();
        EmailType newEmail = instantiateEmail( "some@this.de" );

        // when
        // then
        assertDoesNotThrow( () ->
                clientRegistrationUseCases.register( CLIENT_NAME[2], newEmail,
                        CLIENT_ADDRESS[2] ) );
    }


    @Test
    public void testRegisterClientWithInvalidData() {
        // given
        // when
        // then
        assertThrows( ShopException.class, () ->
                clientRegistrationUseCases.register( null, CLIENT_EMAIL[5],
                        CLIENT_ADDRESS[5] ) );
        assertThrows( ShopException.class, () ->
                clientRegistrationUseCases.register( "", CLIENT_EMAIL[5],
                        CLIENT_ADDRESS[5] ) );
        assertThrows( ShopException.class, () ->
                clientRegistrationUseCases.register( CLIENT_NAME[5], null,
                        CLIENT_ADDRESS[5] ) );
        assertThrows( ShopException.class, () ->
                clientRegistrationUseCases.register( CLIENT_NAME[5], CLIENT_EMAIL[5],
                        null ) );
    }


    @Test
    public void testChangeAddressWithInvalidData() {
        // given
        clientMasterDataInitializer.registerAllClients();

        // when
        // then
        assertThrows( ShopException.class, () ->
                clientRegistrationUseCases.changeAddress( nonExistingEmail, CLIENT_ADDRESS[7] ) );
        assertThrows( ShopException.class, () ->
                clientRegistrationUseCases.changeAddress( null, CLIENT_ADDRESS[7] ) );
        assertThrows( ShopException.class, () ->
                clientRegistrationUseCases.changeAddress( CLIENT_EMAIL[5], null ) );
    }



    @Test
    public void testDeleteClientsNoMoreClients() {
        // given
        clientMasterDataInitializer.registerAllClients();

        // when
        clientRegistrationUseCases.deleteAllClients();

        // then
        assertThrows( ShopException.class, () -> clientRegistrationUseCases.getClientData( CLIENT_EMAIL[0] ) );
    }
}
