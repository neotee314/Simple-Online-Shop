package com.neotee.ecommercesystem.domainprimitives;

import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.io.Serializable;
import java.util.UUID;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@MappedSuperclass
public abstract class GenericId implements Serializable {

    @Column(name = "id", nullable = false)
    protected UUID id;

    protected GenericId(UUID id) {
        if (id == null) {
            throw new DomainValidationException("id", "must not be null");
        }

        this.id = id;
    }

    public String value() {
        return id.toString();
    }

    @Override
    public String toString() {
        return value();
    }
}