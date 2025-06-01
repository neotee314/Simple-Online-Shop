package com.neotee.ecommercesystem.shopsystem.storageunit.domain;

import com.neotee.ecommercesystem.exception.EntityIdNullException;
import com.neotee.ecommercesystem.exception.EntityNotFoundException;
import com.neotee.ecommercesystem.exception.QuantityNegativeException;
import com.neotee.ecommercesystem.exception.ThingQuantityNotAvailableException;
import com.neotee.ecommercesystem.shopsystem.thing.domain.Thing;
import com.neotee.ecommercesystem.shopsystem.thing.domain.ThingId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class StockLevel {
    @Id
    private StockLevelId stockLevelId;

    @ManyToOne
    private Thing thing;

    private Integer quantityInStock = 0;

    public StockLevel(Thing thing, Integer quantityInStock) {
        if (thing == null || quantityInStock == null) throw new EntityNotFoundException();
        if (quantityInStock < 0) throw new QuantityNegativeException();
        this.stockLevelId = new StockLevelId();
        this.thing = thing;
        this.quantityInStock = quantityInStock;

    }

    public boolean contains(Thing thing) {
        if (thing == null) throw new EntityNotFoundException();
        return this.thing.equals(thing);
    }

    public boolean contains(ThingId thingId) {
        if (thingId == null) throw new EntityIdNullException();
        return this.thing.getThingId().getId().equals(thingId.getId());
    }

    public void addToQuantity(Integer quantity) {
        if (quantity == null || quantity < 0) throw new QuantityNegativeException();
        this.quantityInStock += quantity;

    }

    public void removeFromQuantity(Integer removeQuantity) {
        if (removeQuantity == null || removeQuantity < 0) throw new QuantityNegativeException();

        if (quantityInStock < removeQuantity)
            throw new ThingQuantityNotAvailableException();
        quantityInStock -= removeQuantity;
    }

    public void changeStockTo(Integer newTotalQuantity) {
        if (newTotalQuantity == null || newTotalQuantity < 0) throw new QuantityNegativeException();
        quantityInStock = newTotalQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StockLevel that = (StockLevel) o;
        return Objects.equals(getStockLevelId(), that.getStockLevelId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getStockLevelId());
    }
}
