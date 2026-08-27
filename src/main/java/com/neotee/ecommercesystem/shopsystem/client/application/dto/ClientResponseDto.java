package com.neotee.ecommercesystem.shopsystem.client.application.dto;


import java.util.UUID;

public record ClientResponseDto(
    UUID clientId,
    String name,
    String email,
    String street,
    String city,
    String zipCode
) {}