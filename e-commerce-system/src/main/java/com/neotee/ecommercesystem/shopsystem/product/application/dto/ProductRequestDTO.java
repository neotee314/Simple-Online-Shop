package com.neotee.ecommercesystem.shopsystem.product.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductRequestDTO(
    @NotBlank(message = "Name darf nicht leer sein")
    String name,
    
    @NotBlank(message = "Beschreibung darf nicht leer sein")
    String description,
    
    @Positive(message = "Größe muss größer als 0 sein")
    Float size,
    
    @NotNull(message = "Einkaufspreis darf nicht null sein")
    @Positive(message = "Einkaufspreis muss größer als 0 sein")
    Float purchasePrice,
    
    @NotNull(message = "Verkaufspreis darf nicht null sein")
    @Positive(message = "Verkaufspreis muss größer als 0 sein")
    Float salePrice,
    
    @Positive(message = "Lagerbestand muss größer oder gleich 0 sein")
    Integer stockQuantity
) {}