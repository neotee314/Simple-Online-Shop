package com.neotee.ecommercesystem.shopsystem.client.domain;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.HomeAddress;
import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.exception.ValueObjectNullOrEmptyException;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.BasketState;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.domain.ShoppingBasket;
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
    private ClientId clientId ;
    @Embedded
    private Email email;
    @Embedded
    private HomeAddress homeAddress;
    private String name;

    @ElementCollection
    private List<UUID> orderHistory = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "shopping_basket_id", referencedColumnName = "id")
    private ShoppingBasket shoppingBasket;

    public Client(String name, Email email, HomeAddress homeAddress) {
        if (name == null || name.isBlank()) throw new ValueObjectNullOrEmptyException();
        if (email == null || homeAddress == null) throw new ValueObjectNullOrEmptyException();
        this.clientId = new ClientId();
        this.name = name;
        this.email = email;
        this.homeAddress = homeAddress;
        this.shoppingBasket = new ShoppingBasket();
        shoppingBasket.setClientEmail(email);
        shoppingBasket.setBasketState(BasketState.EMPTY);
    }


    public void addToOrderHistory(UUID orderId) {
        if (!orderHistory.contains(orderId)) {
            orderHistory.add(orderId);
        }
    }


    public void changeAddress(HomeAddress address) {
        if (address == null) throw new ValueObjectNullOrEmptyException();
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
