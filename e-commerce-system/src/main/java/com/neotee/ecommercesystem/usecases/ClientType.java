package com.neotee.ecommercesystem.usecases;


import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.HomeAddressType;

/**
 * This interface expresses the essence of a shop client
 */
public interface ClientType {
    public String getName();
    public EmailType getEmail();
    public HomeAddressType getHomeAddress();
}
