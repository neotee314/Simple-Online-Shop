package com.neotee.ecommercesystem.shopsystem.thing.domain;

import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.exception.ValueObjectNullOrEmptyException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Thing {
    @Id
    private ThingId thingId;
    private String name;
    private String description;
    private Float size;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "purchase_price")),
            @AttributeOverride(name = "currency", column = @Column(name = "purchase_currency"))
    })
    private Money purchasePrice;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "sales_price")),
            @AttributeOverride(name = "currency", column = @Column(name = "sales_currency"))
    })
    private Money salesPrice;

    public Thing(UUID thingId, String name, String description, Float size,
                 Money purchasePrice, Money salesPrice) {
        if (thingId == null || name == null || name.isBlank() || description == null || description.isBlank() ||
                (size != null && size <= 0) ||
                purchasePrice == null || salesPrice == null ||
                purchasePrice.getAmount() <= 0 || salesPrice.getAmount() <= 0 ||
                purchasePrice.largerThan(salesPrice)
        )
            throw new ValueObjectNullOrEmptyException();


        this.thingId = new ThingId(thingId);
        this.name = name;
        this.description = description;
        this.size = size;
        this.purchasePrice = purchasePrice;
        this.salesPrice = salesPrice;
    }

    public Money getSellingPrice() {
        return (Money) Money.of(salesPrice.getAmount(), "EUR");
    }


    public UUID getUUID() {
        return thingId.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Thing thing = (Thing) o;
        return Objects.equals(thingId, thing.thingId);
    }

    @Override
    public int hashCode() {
        return thingId != null ? thingId.hashCode() : 0;
    }

}
