package com.neotee.ecommercesystem.solution.payment.application;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.Money;
import com.neotee.ecommercesystem.solution.payment.domain.Payment;
import com.neotee.ecommercesystem.solution.payment.domain.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment createNewPayment(Email clientEmail, Money amount) {
        Payment payment = new Payment(clientEmail, amount);
        return paymentRepository.save(payment);
    }

    public Money getPaymentTotal(Email clientEmail) {
        List<Payment> payments = paymentRepository.findByClientEmail(clientEmail);

        return payments.stream()
                .map(Payment::getMoney)
                .reduce((m1, m2) -> (Money) m1.add(m2))
                .orElse((Money) Money.of(0F, "EUR"));
    }


    @Transactional
    public void deletePayments(Email clientEmail) {
        if (clientEmail == null) {
            throw new ShopException("Invalid client email");
        }

        List<Payment> payments = paymentRepository.findByClientEmail(clientEmail);
        payments.forEach(payment -> paymentRepository.deleteById(payment.getPaymentId()));
    }
}
