package com.neotee.ecommercesystem.core;

public abstract class AggregateRoot<ID> extends AbstractEntity<ID> {

    protected AggregateRoot() {
        super();
    }

    protected AggregateRoot(ID id) {
        super(id);
    }
}