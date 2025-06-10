package com.neotee.ecommercesystem.shopsystem.payment.application.service;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.shopsystem.client.application.service.ClientService;
import com.neotee.ecommercesystem.shopsystem.payment.domain.Payment;
import com.neotee.ecommercesystem.usecases.PaymentUseCases;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.EmailType;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.MoneyType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentUseCaseService implements PaymentUseCases {
    private final PaymentService paymentService;
    private final ClientService clientService;

    /**
     * Authorizes a payment for a client (identified by his/her mail address) for a given amount
     *
     * @param clientEmail
     * @param amount
     * @return the id of the payment, if successfully authorized
     * @throws ShopException if ...
     *                       - clientEmail is null or empty
     *                       - the client with the given email does not exist
     *                       - the amount is <= 0.00 EUR
     *                       - the payment cannot be processed, because it is over the limit of 500.00 EUR
     */

    @Override
    public UUID authorizePayment(EmailType clientEmail, MoneyType amount) {
        if (clientEmail == null || clientEmail.toString().isEmpty() || amount == null || amount.getAmount() <= 0)
            throw new ShopException("invalid data");
        Payment payment = paymentService.createNewPayment((Email) clientEmail, (Money) amount);
        return payment.getPaymentId().getId();

    }

    /**
     * Returns the total amount of payments (over the complete history) for a client
     * (identified by his/her email)
     *
     * @param clientEmail
     * @return the total amount of payments for the client with the given email,
     * or 0.00 EUR if the client has not made any payments yet.
     * @throws ShopException if ...
     *                       - clientEmail is null or empty
     *                       - the client with the given email does not exist
     */

    @Override
    public MoneyType getPaymentTotal(EmailType clientEmail) {
        if (clientEmail == null || clientEmail.toString().isEmpty()) throw new ShopException("invalid data");
        return paymentService.getPaymentTotal((Email) clientEmail);

    }


    /**
     * Deletes all payment history, for all clients.
     */
    @Override
    public void deletePaymentHistory() {
        List<Email> clients = clientService.findAll();
        clients.forEach(clientEmail -> paymentService.deletePayments(clientEmail));
    }
}
