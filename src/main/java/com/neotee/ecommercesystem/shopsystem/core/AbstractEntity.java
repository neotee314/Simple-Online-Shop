package com.neotee.ecommercesystem.shopsystem.core;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class AbstractEntity<ID> {

    @EmbeddedId
    protected ID id;

    protected AbstractEntity() {
    }

    protected AbstractEntity(ID id) {
        this.id = id;
    }
}