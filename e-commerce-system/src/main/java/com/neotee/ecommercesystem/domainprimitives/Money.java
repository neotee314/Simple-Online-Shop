package com.neotee.ecommercesystem.domainprimitives;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.MoneyType;
import lombok.Getter;
import lombok.NoArgsConstructor;


import jakarta.persistence.*;
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
            throw new ShopException("Amount must be non-null and >= 0");
        }
        if (currency == null || currency.trim().isEmpty()) {
            throw new ShopException("Currency must not be null or empty");
        }
        if (!ALLOWED_CURRENCIES.contains(currency)) {
            throw new ShopException("Currency must be one of: " + ALLOWED_CURRENCIES);
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
            throw new ShopException("Cannot subtract more than the current amount");
        }
        return new Money(this.amount - other.getAmount(), this.currency);
    }

    @Override
    public MoneyType multiplyBy(int factor) {
        if (factor < 0) {
            throw new ShopException("Factor must be >= 0");
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
            throw new ShopException("Other money cannot be null");
        }
        if (!this.currency.equals(other.getCurrency())) {
            throw new ShopException("Currency mismatch: " + this.currency + " vs " + other.getCurrency());
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
