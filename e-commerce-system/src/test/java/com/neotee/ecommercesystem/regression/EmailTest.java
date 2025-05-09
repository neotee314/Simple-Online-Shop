package com.neotee.ecommercesystem.regression;

import org.junit.jupiter.api.Test;
import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.usecases.*;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.*;
import com.neotee.ecommercesystem.usecases.masterdata.*;

import static com.neotee.ecommercesystem.usecases.masterdata.FactoryMethodInvoker.instantiateEmail;
import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    public void testToStringEmail() {
        // given
        String input = "test@example.com";

        // when
        EmailType instance = instantiateEmail( input );

        // then
        assertEquals( input, instance.toString() );
    }

    @Test
    public void testEqualityEmail() {
        // given
        // when
        EmailType instance1 = instantiateEmail( "test@example.com" );
        EmailType instance2 = instantiateEmail( "test@example.com" );
        EmailType instance3 = instantiateEmail( "different@example.com" );

        // then
        assertEquals( instance1, instance2 );
        assertNotEquals( instance1, instance3 );
    }

    @Test
    public void testImmutabilityEmail() {
        // given
        // when
        EmailType instance = instantiateEmail( "test@example.com" );

        // then
        try {
            instance.getClass().getMethod( "setEmail", String.class );
            fail( "setEmail method should not exist" );
        } catch (NoSuchMethodException e) {
            // Success: the object is immutable
        }
    }

    @Test
    public void testFactoryValidEmail() {
        // given
        // when
        // then
        assertDoesNotThrow( () -> instantiateEmail( "test@example.com" ) );
        assertDoesNotThrow( () -> instantiateEmail( "99Z@example.com" ) );
        assertDoesNotThrow( () -> instantiateEmail( "GGGhh@s77.com" ) );
        assertDoesNotThrow( () -> instantiateEmail( "a@4.com" ) );
        assertDoesNotThrow( () -> instantiateEmail( "Max.Hammer@example.com" ) );
        assertDoesNotThrow( () -> instantiateEmail( "Max.Gideon.Hammer@example.com" ) );
        assertDoesNotThrow( () -> instantiateEmail( "test@example.this.com" ) );
        assertDoesNotThrow( () -> instantiateEmail( "test@example.de" ) );
        assertDoesNotThrow( () -> instantiateEmail( "test@example.at" ) );
        assertDoesNotThrow( () -> instantiateEmail( "test@example.ch" ) );
        assertDoesNotThrow( () -> instantiateEmail( "test@example.org" ) );
    }

    @Test
    public void testFactoryInvalidEmail() {
        // given
        // when
        // then
        assertThrows( ShopException.class, () -> instantiateEmail( null ) );
        assertThrows( ShopException.class, () -> instantiateEmail( "testexample.com" ) );
        assertThrows( ShopException.class, () -> instantiateEmail( "test@" ) );
        assertThrows( ShopException.class, () -> instantiateEmail( "@example.com" ) );
        assertThrows( ShopException.class, () -> instantiateEmail( "Max..Gideon.Hammer@example.com" ) );
        assertThrows( ShopException.class, () -> instantiateEmail( "test@examplecom" ) );
        assertThrows( ShopException.class, () -> instantiateEmail( "test@example..com" ) );
        assertThrows( ShopException.class, () -> instantiateEmail( "test@example@that.com" ) );
        assertThrows( ShopException.class, () -> instantiateEmail( "test example@that.com" ) );
        assertThrows( ShopException.class, () -> instantiateEmail( "test#example@that.com" ) );
        assertThrows( ShopException.class, () -> instantiateEmail( "test@example.biz" ) );
        assertThrows( ShopException.class, () -> instantiateEmail( "test@example.biz" ) );
        assertThrows( ShopException.class, () -> instantiateEmail( "test@example.42" ) );
    }

}
