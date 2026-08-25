package com.neotee.ecommercesystem.domainprimitives;

import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.HomeAddressType;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.ZipCodeType;
import jakarta.persistence.Embedded;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
public class HomeAddress implements HomeAddressType {

    @Embedded
    private ZipCode zipCode;

    private String street;
    private String city;

    private HomeAddress(String street, String city, ZipCodeType zipCode) {
        validate(street, city, zipCode);
        this.street = street.trim();
        this.city = city.trim();
        this.zipCode = (ZipCode) zipCode;
    }

    public static HomeAddressType of(String street, String city, ZipCodeType zipCode) {
        return new HomeAddress(street, city, zipCode);
    }

    private void validate(String street, String city, ZipCodeType zipCode) {
        if (street == null || street.trim().isEmpty()) {
            throw new DomainValidationException("street", "Straßenname darf nicht leer sein.");
        }
        if (city == null || city.trim().isEmpty()) {
            throw new DomainValidationException("city", "Stadt darf nicht leer sein.");
        }
        if (zipCode == null) {
            throw new DomainValidationException("zipCode", "Postleitzahl darf nicht null sein.");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HomeAddressType)) return false;
        HomeAddressType other = (HomeAddressType) obj;
        return Objects.equals(street, other.getStreet()) &&
                Objects.equals(city, other.getCity()) &&
                Objects.equals(zipCode, other.getZipCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, zipCode);
    }
}