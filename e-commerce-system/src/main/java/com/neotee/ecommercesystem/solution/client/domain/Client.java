package com.neotee.ecommercesystem.solution.client.domain;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.HomeAddress;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Client {
    @Id
    private UUID clientId = UUID.randomUUID();
    @Embedded
    private Email email;
    @Embedded
    private HomeAddress homeAddress;
    private String name;

    @ElementCollection
    private List<UUID> orderHistory = new ArrayList<>();

    @ElementCollection
    private List<UUID> shoppingBasket = new ArrayList<>();

    public Client(String name, Email email, HomeAddress homeAddress) {
        if (name == null || name.isBlank()) throw new ShopException("Invalid name");
        if (email == null || homeAddress == null) throw new ShopException("Email/address cannot be null");

        this.name = name;
        this.email = email;
        this.homeAddress = homeAddress;
    }

    public void addToShoppingBasket(UUID thingId) {
        if (!shoppingBasket.contains(thingId)) {
            shoppingBasket.add(thingId);
        }
    }

    public void removeFromShoppingBasket(UUID thingId) {
        shoppingBasket.remove(thingId);
    }

    public void addToOrderHistory(UUID orderId) {
        if (!orderHistory.contains(orderId)) {
            orderHistory.add(orderId);
        }
    }


    public void changeAddress(HomeAddress address) {
        if (address == null) throw new ShopException("Address cannot be null");
        this.setHomeAddress(address);
    }

    public void updateName(String newName) {
        this.name = newName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(clientId, client.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId);
    }
}
