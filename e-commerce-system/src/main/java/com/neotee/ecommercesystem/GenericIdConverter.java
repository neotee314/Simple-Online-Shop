/*
 * Copyright ArchiLab 2025. All rights reserved.
 */
package com.neotee.ecommercesystem;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.UUID;
import java.util.function.Function;


@Converter
public abstract class GenericIdConverter<T extends GenericId> implements AttributeConverter<T, UUID> {
    private final Function<UUID, T> factory;

    protected GenericIdConverter( Function<UUID, T> factory ) {
        this.factory = factory;
    }

    @Override
    public UUID convertToDatabaseColumn( T attribute ) {
        return attribute == null ? null : attribute.getId();
    }

    @Override
    public T convertToEntityAttribute( UUID dbData ) {
        return dbData == null ? null : factory.apply( dbData );
    }
}
