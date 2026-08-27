package com.neotee.ecommercesystem.restdtos;

import java.util.UUID;

public record ClientResponseDto(UUID clientId, String name, String email, String street, String city, String zipCode) {}