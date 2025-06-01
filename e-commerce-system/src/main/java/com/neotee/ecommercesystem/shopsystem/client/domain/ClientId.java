package com.neotee.ecommercesystem.shopsystem.client.domain;

import com.neotee.ecommercesystem.GenericId;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class ClientId extends GenericId {
    protected ClientId() { super(); }
    public ClientId(UUID id) { super(id); }
}
