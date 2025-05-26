package com.neotee.ecommercesystem.solution.shoppingbasket.domain;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.MoneyType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.neotee.ecommercesystem.solution.shoppingbasket.domain.BasketState.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "SHOPPING_BASKET")
public class ShoppingBasket {

    @Id
    private UUID id;

    private Email clientEmail;

    private BasketState basketState;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ShoppingBasketPart> parts = new ArrayList<>();


    public ShoppingBasket() {
        this.id = UUID.randomUUID();
    }

    // Add a new part to the shopping basket
    private void addPart(ShoppingBasketPart newPart) {
        if (newPart == null || newPart.getQuantity() <= 0) {
            throw new ShopException("Quantity must be greater than zero.");
        }
        this.setBasketState(FILLED);

        for (ShoppingBasketPart existingPart : parts) {
            if (existingPart.equals(newPart)) {
                existingPart.increaseQuantity(newPart.getQuantity());
                return;
            }
        }
        parts.add(newPart);
    }

    public int removeReservedItems(UUID thingId, Integer quantityToRemove) {
        int reserved = getReservedQuantityForThing(thingId);
        int removed = Math.min(reserved, quantityToRemove);

        for (int i = 0; i < removed; i++) {
            removeItem(thingId, 1);
        }

        return removed;
    }


    // Check if the shopping basket is empty
    public boolean isEmpty() {
        return parts.stream().allMatch(part -> part.getQuantity() == 0) || basketState == EMPTY;
    }


    public Integer getReservedQuantityForThing(UUID thingId) {
        if (thingId == null) {
            throw new ShopException("thingId cannot be null");
        }
        return parts.stream()
                .filter(part -> part.contains(thingId))
                .mapToInt(ShoppingBasketPart::getQuantity)
                .sum();
    }

    public void addItem(UUID thingId, Integer quantity, Money price) {
        if (thingId == null || quantity < 0 || price == null || price.getAmount() < 0)
            throw new ShopException("Invalid thing ID or quantity must be greater than 0");
        // Add item to the shopping basket
        ShoppingBasketPart part = new ShoppingBasketPart(thingId, quantity, price);
        addPart(part);
    }

    public void removeItem(UUID thingId, Integer quantity) {
        if (thingId == null || quantity <= 0)
            throw new ShopException("Invalid thing ID or quantity must be greater than 0");

        for (ShoppingBasketPart existingPart : parts) {
            if (existingPart.contains(thingId)) {
                int newQuantity = existingPart.getQuantity() - quantity;
                if (newQuantity > 0) {
                    existingPart.decreaseQuantity(quantity);
                    return;
                } else if (newQuantity == 0) {
                    parts.remove(existingPart);
                    return;
                } else throw new ShopException("Cannot remove more than existing quantity.");

            }
        }
    }

    public void removeItem(UUID thingId) {
        if (thingId == null)
            throw new ShopException("Invalid thing ID or quantity must be greater than 0");

        for (ShoppingBasketPart existingPart : parts) {
            if (existingPart.contains(thingId)) {
                parts.remove(existingPart);
                return;
            }
        }
    }

    public Map<UUID, Integer> getAsMap() {
        return parts.stream()
                .collect(Collectors.toMap(ShoppingBasketPart::getThingId, ShoppingBasketPart::getQuantity));
    }

    public Money getAsMoneyValue() {
        MoneyType totalValue = Money.of(0f, "EUR");
        Money.of(0f, "EUR");
        for (ShoppingBasketPart part : parts) {
            Money price = part.getSalesPrice();
            totalValue = totalValue.add(price.multiplyBy(part.getQuantity()));
        }
        return (Money) totalValue;
    }

    public void checkout() {
        parts.clear();
    }

    public Map<UUID, Integer> getPartsAsMapValue() {
        return parts.stream()
                .collect(Collectors.toMap(ShoppingBasketPart::getThingId, ShoppingBasketPart::getQuantity));
    }

    public boolean contains(UUID thingId) {
        if (thingId == null) throw new ShopException("Thing ID must not be null");
        return parts.stream()
                .anyMatch(part -> part.contains(thingId));
    }

    public void clear() {
        parts.clear();
        this.basketState = EMPTY;
    }

    public Money getTotalSalesPrice() {
        MoneyType totalSalesPrice = Money.of(0f, "EUR");
        for (ShoppingBasketPart part : parts) {
            Money price = part.getSalesPrice();
            totalSalesPrice = totalSalesPrice.add(price.multiplyBy(part.getQuantity()));
        }
        return (Money) totalSalesPrice;
    }
}
