package com.neotee.ecommercesystem.shopsystem.payment.domain;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.Money;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Payment {
    @Id
    private PaymentId paymentId;

    private Email clientEmail;

    @Embedded
    private Money money;
    
    public Payment(Email clientEmail, Money money) {
        this.paymentId = new PaymentId();
        this.clientEmail = clientEmail;
        this.money = money;
    }

    public void addToAmount(Money addedAmount) {
        if(addedAmount==null || addedAmount.getAmount()<=0) throw new ShopException("Added amount must be greater than zero");
        this.money = (Money) money.add(addedAmount);
    }

    public void removeFromAmount(Money amountToRemove) {
        if(amountToRemove==null || amountToRemove.getAmount()<=0) throw new ShopException("Added amount must be greater than zero");
        this.money = (Money) money.subtract(amountToRemove);
    }


}
