package com.neotee.ecommercesystem.shopsystem.client.domain;


import com.neotee.ecommercesystem.usecases.ClientType;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.HomeAddressType;

public class ClientTypeImp implements ClientType {
    private final String name;
    private final EmailType email;
    private final HomeAddressType homeAddress;

    public ClientTypeImp(String name, EmailType email, HomeAddressType homeAddress) {
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
}
