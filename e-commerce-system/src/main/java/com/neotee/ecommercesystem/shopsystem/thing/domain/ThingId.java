package com.neotee.ecommercesystem.shopsystem.thing.domain;

import com.neotee.ecommercesystem.GenericId;
import jakarta.persistence.Embeddable;


import java.util.UUID;

@Embeddable
public class ThingId extends GenericId {
    protected ThingId() { super(); }
    public ThingId(UUID id) { super(id); }


}
