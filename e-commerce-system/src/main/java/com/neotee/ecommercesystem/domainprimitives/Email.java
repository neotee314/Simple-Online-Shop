package com.neotee.ecommercesystem.domainprimitives;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.Objects;
import java.util.regex.Pattern;

@Getter
@NoArgsConstructor
@Embeddable
public class Email implements EmailType {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final String[] VALID_DOMAINS = {".de", ".at", ".ch", ".com", ".org"};

    private String emailAddress;

    private Email(String emailAddress) {
        validate(emailAddress);
        this.emailAddress = emailAddress;
    }

    public static Email of(String emailAsString) {
        return new Email(emailAsString);
    }

    private void validate(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ShopException("E-Mail-Adresse darf nicht leer sein.");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ShopException("Ungültiges E-Mail-Format.");
        }

        String[] parts = email.split("@");
        if (parts.length != 2) {
            throw new ShopException("E-Mail muss genau ein '@' enthalten.");
        }

        String localPart = parts[0];
        String domainPart = parts[1];

        if (localPart.isEmpty() || domainPart.isEmpty()) {
            throw new ShopException("Lokaler Teil oder Domain fehlt.");
        }

        if (localPart.contains(" ") || domainPart.contains(" ")) {
            throw new ShopException("E-Mail darf keine Leerzeichen enthalten.");
        }

        if (localPart.contains("..") || domainPart.contains("..")) {
            throw new ShopException("E-Mail darf keine doppelten Punkte enthalten.");
        }

        boolean domainValid = false;
        for (String validSuffix : VALID_DOMAINS) {
            if (domainPart.endsWith(validSuffix)) {
                domainValid = true;
                break;
            }
        }

        if (!domainValid) {
            throw new ShopException("Ungültige Domain-Endung.");
        }
    }

    @Override
    public EmailType sameIdentifyerDifferentDomain(String newDomain) {
        if (newDomain == null || newDomain.trim().isEmpty()) {
            throw new ShopException("Domain darf nicht leer sein.");
        }

        String localPart = emailAddress.split("@")[0];
        return new Email(localPart + "@" + newDomain);
    }

    @Override
    public EmailType sameDomainDifferentIdentifyer(String newIdentifyer) {
        if (newIdentifyer == null || newIdentifyer.trim().isEmpty()) {
            throw new ShopException("Benutzername darf nicht leer sein.");
        }

        String domainPart = emailAddress.split("@")[1];
        return new Email(newIdentifyer + "@" + domainPart);
    }

    @Override
    public String toString() {
        return emailAddress;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Email)) return false;
        Email other = (Email) obj;
        return Objects.equals(emailAddress, other.emailAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emailAddress);
    }
}
