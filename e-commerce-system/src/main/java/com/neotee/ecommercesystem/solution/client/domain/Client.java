package com.neotee.ecommercesystem.solution.client.domain;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.HomeAddress;
import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.solution.shoppingbasket.domain.ShoppingBasket;
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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private ShoppingBasket shoppingBasket;

    public Client(String name, Email email, HomeAddress homeAddress) {
        if (name == null || name.isBlank()) throw new ShopException("Invalid name");
        if (email == null || homeAddress == null) throw new ShopException("Email/address cannot be null");

        this.name = name;
        this.email = email;
        this.homeAddress = homeAddress;
        this.shoppingBasket = new ShoppingBasket();
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

    public ZipCode findZipCode() {
        return homeAddress.getZipCode();
    }

    public void updateName(String newName) {
        this.name = newName;
    }

    public void deleteBasket() {
        this.shoppingBasket = null;
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
