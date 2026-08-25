package com.neotee.ecommercesystem;


import com.neotee.ecommercesystem.exceptions.ShopException;
import com.neotee.ecommercesystem.usecases.ClientRegistrationUseCases;
import com.neotee.ecommercesystem.usecases.ClientType;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.HomeAddressType;
import com.neotee.ecommercesystem.usecases.masterdata.MockClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This is a test helper class that initializes and registers clients in the system,
 * using the given interface(s).
 */

@Slf4j
@SuppressWarnings("PMD")
@Component
public class ClientMasterDataInitializer {

    private ClientRegistrationUseCases clientRegistrationUseCases;
    private Random random = new Random();

    public static final String EUR = "EUR";

    @Autowired
    public ClientMasterDataInitializer( ClientRegistrationUseCases clientRegistrationUseCases ) {
        this.clientRegistrationUseCases = clientRegistrationUseCases;
    }

    public final static String[] CLIENT_NAME = new String[]{
            "Max Müller",
            "Sophie Schmitz",
            "Irene Mihalic",
            "Emilia Fischer",
            "Filiz Polat",
            "Lina Wagner",
            "Leon Becker",
            "Agnieszka Kalterer",
            "Felix Bauer",
            "Lara Schulz"
    };

    public final static EmailType[] CLIENT_EMAIL = new EmailType[]{
            com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateEmail( "99Z@example.com" ),
            com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateEmail( "a@4.com" ),
            com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateEmail( "irene@wearefreedomnow.com" ),
            com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateEmail( "emilia.fischer@example.com" ),
            com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateEmail( "j877d3@example.this.com" ),
            com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateEmail( "lina.marie.wagner@example.com" ),
            com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateEmail( "0.1.2.3.4.5.6.7.8.becker@example.com" ),
            com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateEmail( "agna@here.ch" ),
            com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateEmail( "felix.bauer@example.org" ),
            com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateEmail( "lara.schulz@example.at" )
    };

    // The following array is used to create a list of homeAddresss for the clients.
    // The indices are coded into the house number.
    // - Persons 0 - 5 are used for proximity tests with one delivery package.
    public final static HomeAddressType[] CLIENT_ADDRESS = new HomeAddressType[]{
            com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateHomeAddress(
                    "Marktstraße 0", "Viertelstadt",
                    com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateZipCode( "02314" ) ),
            com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateHomeAddress(
                    "Hauptstraße 1", "Viertelstadt",
                    com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateZipCode( "02368" ) ),
            com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateHomeAddress(
                    "Kirchplatz 2", "Niemandstown",
                    com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateZipCode( "12345" ) ),
            com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateHomeAddress(
                    "Schulstraße 3", "Geisterhausen",
                    com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateZipCode( "31463" ) ),
            com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateHomeAddress(
                    "Rosenweg 4", "Kuhhausen",
                    com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateZipCode( "72162" ) ),
            com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateHomeAddress(
                    "Wiesenstraße 5", "Waldschenkensdorf",
                    com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateZipCode( "82195" ) ),
            com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateHomeAddress(
                    "Mühlenweg 6", "Köln",
                    com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateZipCode( "50667" ) ),
            com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateHomeAddress(
                    "Goethestraße 7", "Frankfurt am Main",
                    com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateZipCode( "60311" ) ),
            com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateHomeAddress(
                    "Dorfstraße 8", "Stuttgart",
                    com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateZipCode( "70173" ) ),
            com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateHomeAddress(
                    "Bahnhofstraße 9", "München",
                    com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateZipCode( "80331" ) )
    };

    public final static ClientType[] mockClients;

    static {
        mockClients = new ClientType[CLIENT_NAME.length];
        for ( int i = 0; i < CLIENT_NAME.length; i++ ) {
            mockClients[i] = new com.neotee.ecommercesystem.usecases.masterdata.MockClient(
                    CLIENT_NAME[i], CLIENT_EMAIL[i], CLIENT_ADDRESS[i] );
        }
        // TODO - remove and replace by current _norepo, in M3
        assertThrows( ShopException.class, () -> {
            new com.neotee.ecommercesystem.usecases.masterdata.MockClient( "No one",
                    com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateEmail( null ),
                    CLIENT_ADDRESS[0] ); }, "Check your email validation!"  );
        assertThrows( ShopException.class, () -> {
            new com.neotee.ecommercesystem.usecases.masterdata.MockClient( "No one",
                    com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateEmail( "Max..Gideon.Hammer@example.com" ),
                    CLIENT_ADDRESS[0] ); }, "Check your email validation!" );
        assertThrows( ShopException.class, () -> {
            new com.neotee.ecommercesystem.usecases.masterdata.MockClient( "No one",
                    com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateEmail( "test@example.42" ),
                    CLIENT_ADDRESS[0] ); }, "Check your email validation!"  );
        assertThrows( ShopException.class, () -> {
            new MockClient( "No one",
                    FactoryMethodInvoker.instantiateEmail( "test@" ),
                    CLIENT_ADDRESS[0] ); }, "Check your email validation!"  );


    }


    public void registerAllClients() {
        log.info( "Registering clients.");
        for ( int i = 0; i < CLIENT_NAME.length; i++ ) {
            registerClient( CLIENT_NAME[i], CLIENT_EMAIL[i], CLIENT_ADDRESS[i] );
        }
    }

    public void registerClient( String name, EmailType email, HomeAddressType homeAddress ) {
        log.info( "Registering client with email " + email );
        clientRegistrationUseCases.register( name, email, homeAddress );
    }
}
