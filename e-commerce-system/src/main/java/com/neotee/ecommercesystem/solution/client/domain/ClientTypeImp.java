package com.neotee.ecommercesystem.solution.client.domain;


import com.neotee.ecommercesystem.usecases.ClientType;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.HomeAddressType;

public class ClientTypeImp implements ClientType {
    private String name;
    private EmailType email;
    private HomeAddressType homeAddress;

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
