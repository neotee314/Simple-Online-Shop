/*
 * Copyright ArchiLab 2025. All rights reserved.
 */
package com.neotee.ecommercesystem;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@MappedSuperclass
@Getter
@EqualsAndHashCode(of = "id")
public abstract class GenericId implements Serializable {
    @Column(nullable = false, updatable = false)
    private final UUID id;

    protected GenericId() {
        this( UUID.randomUUID() );
    }

    protected GenericId( UUID id ) {
        this.id = id;
    }
}
