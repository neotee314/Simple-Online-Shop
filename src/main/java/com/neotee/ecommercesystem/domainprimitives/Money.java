package com.neotee.ecommercesystem.domainprimitives;

import com.neotee.ecommercesystem.exceptions.DomainValidationException;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.MoneyType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Embeddable;
import java.util.List;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
public class Money implements MoneyType {

    private Float amount;
    private String currency;

    private static final List<String> ALLOWED_CURRENCIES = List.of("EUR", "CHF");

    private Money(Float amount, String currency) {
        validate(amount, currency);
        this.amount = amount;
        this.currency = currency;
    }

    public static MoneyType of(Float amount, String currency) {
        return new Money(amount, currency);
    }

    private void validate(Float amount, String currency) {
        if (amount == null || amount < 0) {
            throw new DomainValidationException("amount", "Amount must be non-null and >= 0");
        }
        if (currency == null || currency.trim().isEmpty()) {
            throw new DomainValidationException("currency", "Currency must not be null or empty");
        }
        if (!ALLOWED_CURRENCIES.contains(currency)) {
            throw new DomainValidationException("currency", "Currency must be one of: " + ALLOWED_CURRENCIES);
        }
    }

    @Override
    public MoneyType add(MoneyType other) {
        checkSameCurrency(other);
        return new Money(this.amount + other.getAmount(), this.currency);
    }

    @Override
    public MoneyType subtract(MoneyType other) {
        checkSameCurrency(other);
        if (this.amount < other.getAmount()) {
            throw new DomainValidationException("amount", "Cannot subtract more than the current amount");
        }
        return new Money(this.amount - other.getAmount(), this.currency);
    }

    @Override
    public MoneyType multiplyBy(int factor) {
        if (factor < 0) {
            throw new DomainValidationException("factor", "Factor must be >= 0");
        }
        return new Money(this.amount * factor, this.currency);
    }

    @Override
    public boolean largerThan(MoneyType other) {
        checkSameCurrency(other);
        return this.amount > other.getAmount();
    }

    private void checkSameCurrency(MoneyType other) {
        if (other == null) {
            throw new DomainValidationException("currency", "Other money cannot be null");
        }
        if (!this.currency.equals(other.getCurrency())) {
            throw new DomainValidationException("currency", "Currency mismatch: " + this.currency + " vs " + other.getCurrency());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MoneyType)) return false;
        MoneyType other = (MoneyType) obj;
        return Float.compare(amount, other.getAmount()) == 0 &&
                Objects.equals(currency, other.getCurrency());
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
}