package com.neotee.ecommercesystem.usecases.masterdata;

import com.neotee.ecommercesystem.usecases.ClientType;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.HomeAddressType;
import lombok.Setter;

import java.util.Objects;

@Setter
public class MockClient implements ClientType {
    private String name;
    private EmailType email;
    private HomeAddressType homeAddress;


    public MockClient( String name, EmailType email, HomeAddressType homeAddress) {
        this.name = name;
        this.email = email;
        this.homeAddress = homeAddress;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public EmailType getEmail() {
        return email;
    }

    @Override
    public HomeAddressType getHomeAddress() {
        return homeAddress;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof MockClient ) ) return false;
        MockClient that = (MockClient) o;
        return Objects.equals( getName(), that.getName() ) && Objects.equals( email, that.email ) && Objects.equals( homeAddress, that.homeAddress );
    }

    @Override
    public int hashCode() {
        return Objects.hash( getName(), email, homeAddress );
    }
}
