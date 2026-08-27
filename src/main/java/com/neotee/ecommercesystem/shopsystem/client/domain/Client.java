package com.neotee.ecommercesystem.shopsystem.client.domain;

import com.neotee.ecommercesystem.domainprimitives.ClientId;
import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.HomeAddress;
import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.shopsystem.core.AggregateRoot;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Client extends AggregateRoot<ClientId> {

    @Embedded
    private Email email;

    @Embedded
    private HomeAddress homeAddress;

    @Setter
    private String name;


    protected Client(ClientId clientId) {
        this.id = clientId;
    }

    public static Client create() {
        return new Client(ClientId.newId());
    }

    public static Client create(ClientId clientId) {
        return new Client(clientId);
    }

    public static Client create(String name, Email email, HomeAddress homeAddress) {
        var client = Client.create(ClientId.newId());
        client.name = name;
        client.email = email;
        client.homeAddress = homeAddress;
        return client;
    }

    public static Client create(ClientId clientId, String name, Email email, HomeAddress homeAddress) {
        var client = Client.create(clientId);
        client.name = name;
        client.email = email;
        client.homeAddress = homeAddress;
        return client;
    }

    public void changeAddress(HomeAddress address) {
        if (address == null) {
            throw new DomainValidationException("homeAddress", "Adresse darf nicht null sein.");
        }
        this.homeAddress = address;
    }

    public ZipCode findZipCode() {
        return homeAddress != null ? homeAddress.getZipCode() : null;
    }

    public void updateName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new DomainValidationException("name", "Name darf nicht leer sein.");
        }
        this.name = newName;
    }

    public void setEmail(Email email) {
        if (email == null) {
            throw new DomainValidationException("email", "E-Mail darf nicht null sein.");
        }
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}