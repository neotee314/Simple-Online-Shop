package com.neotee.ecommercesystem.shopsystem.client.application.dto;

import com.neotee.ecommercesystem.domainprimitives.ClientId;

public record ClientResponseDto(
    ClientId clientId,
    String name,
    String email,
    String street,
    String city,
    String zipCode
) {}