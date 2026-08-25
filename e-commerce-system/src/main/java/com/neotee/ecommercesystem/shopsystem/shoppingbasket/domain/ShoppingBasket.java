package com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain;

import com.neotee.ecommercesystem.core.AggregateRoot;
import com.neotee.ecommercesystem.domainprimitives.BasketState;
import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.domainprimitives.ProductId;
import com.neotee.ecommercesystem.domainprimitives.ShoppingBasketId;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.client.domain.Client;
import com.neotee.ecommercesystem.shopsystem.product.domain.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Table(name = "shopping_basket")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ShoppingBasket extends AggregateRoot<ShoppingBasketId> {

    @OneToOne
    private Client client;

    @Enumerated(EnumType.STRING)
    private BasketState basketState;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "shopping_basket_id")
    private List<ShoppingBasketPart> parts;

    protected ShoppingBasket(ShoppingBasketId basketId) {
        this.id = basketId;
        this.basketState = BasketState.EMPTY;
        this.parts = new ArrayList<>();
    }

    protected ShoppingBasket(ShoppingBasketId basketId, Client client) {
        this.id = basketId;
        this.client = client;
        this.basketState = BasketState.EMPTY;
        this.parts = new ArrayList<>();
    }

    public static ShoppingBasket create(Client client) {
        if (client == null)
            throw new DomainValidationException("ShoppingBasket", "Client darf nicht null sein.");
        return new ShoppingBasket(ShoppingBasketId.newId(), client);
    }

    public static ShoppingBasket create(ShoppingBasketId basketId, Client client) {
        if (basketId == null)
            throw new DomainValidationException("ShoppingBasket", "Basket ID darf nicht null sein.");
        if (client == null)
            throw new DomainValidationException("ShoppingBasket", "Client darf nicht null sein.");
        return new ShoppingBasket(basketId, client);
    }

    public void addItem(Product product, Integer quantity) {
        if (product == null)
            throw new DomainValidationException("ShoppingBasket", "Product darf nicht null sein.");
        if (quantity == null || quantity <= 0)
            throw new DomainValidationException("ShoppingBasket", "Quantity muss größer als 0 sein.");

        var existingPart = findPartByProduct(product);
        if (existingPart != null) {
            existingPart.increaseQuantity(quantity);
            updateBasketState();
            return;
        }

        var newPart = ShoppingBasketPart.create(product, quantity, product.getPurchasePrice());
        parts.add(newPart);
        updateBasketState();
    }

    public void removeItem(Product product) {
        if (product == null)
            throw new DomainValidationException("ShoppingBasket", "Product darf nicht null sein.");
        var part = findPartByProduct(product);
        if (part == null)
            throw new DomainValidationException("ShoppingBasket", "Produkt nicht im Warenkorb gefunden.");
        parts.remove(part);
        updateBasketState();
    }

    public void removeItem(Product product, Integer quantity) {
        if (product == null)
            throw new DomainValidationException("ShoppingBasket", "Product darf nicht null sein.");
        if (quantity == null || quantity <= 0)
            throw new DomainValidationException("ShoppingBasket", "Quantity muss größer als 0 sein.");

        var part = findPartByProduct(product);
        if (part == null)
            throw new DomainValidationException("ShoppingBasket", "Produkt nicht im Warenkorb gefunden.");

        var newQuantity = part.getQuantity() - quantity;
        if (newQuantity > 0) {
            part.decreaseQuantity(quantity);
        } else if (newQuantity == 0) {
            parts.remove(part);
        } else {
            throw new DomainValidationException("ShoppingBasket", "Kann nicht mehr entfernen als vorhanden ist.");
        }
        updateBasketState();
    }

    public void clear() {
        parts.clear();
        this.basketState = BasketState.EMPTY;
    }

    public boolean isEmpty() {
        return parts.isEmpty() || basketState == BasketState.EMPTY;
    }

    public Money getTotalPrice() {
        var total = Money.of(0f, "EUR");
        for (var part : parts) {
            total = (Money) total.add(part.getSalesPrice());
        }
        return (Money) total;
    }

    public boolean contains(Product product) {
        if (product == null)
            throw new DomainValidationException("ShoppingBasket", "Product darf nicht null sein.");
        return findPartByProduct(product) != null;
    }

    public int getTotalQuantity() {
        return parts.stream()
                .mapToInt(ShoppingBasketPart::getQuantity)
                .sum();
    }

    public List<ShoppingBasketPart> getParts() {
        return new ArrayList<>(parts);
    }

    public Integer getReservedQuantityForProduct(ProductId productId) {
        if (productId == null)
            throw new DomainValidationException("ShoppingBasket", "Product ID darf nicht null sein.");

        return parts.stream()
                .filter(part -> part.getProduct().getId().equals(productId))
                .mapToInt(ShoppingBasketPart::getQuantity)
                .sum();
    }

    public boolean hasItems() {
        return !parts.isEmpty();
    }

    private ShoppingBasketPart findPartByProduct(Product product) {
        if (product == null) return null;
        return parts.stream()
                .filter(part -> part.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);
    }

    private void updateBasketState() {
        this.basketState = parts.isEmpty() ? BasketState.EMPTY : BasketState.FILLED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (ShoppingBasket) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}