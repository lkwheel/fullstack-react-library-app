package com.wheelerkode.library.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.wheelerkode.library.dao.PaymentRepository;
import com.wheelerkode.library.entity.Payment;
import com.wheelerkode.library.models.NotFoundException;
import com.wheelerkode.library.requestmodels.PaymentInfoRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;


    public PaymentService(PaymentRepository paymentRepository, @Value("${stripe.key.secret}") String secretKey) {
        this.paymentRepository = paymentRepository;
        Stripe.apiKey = secretKey;
    }

    public PaymentIntent createPaymentIntent(PaymentInfoRequest paymentInfoRequest) throws StripeException {
        List<String> paymentMethodTypes = new ArrayList<>();
        paymentMethodTypes.add("card");

        Map<String, Object> params = new HashMap<>();
        params.put("amount", paymentInfoRequest.getAmount());
        params.put("currency", paymentInfoRequest.getCurrency());
        params.put("payment_method_types", paymentMethodTypes);

        return PaymentIntent.create(params);
    }

    public ResponseEntity<String> stripePayment(String userEmail) throws Exception {
        Optional<Payment> payment = paymentRepository.findBooksByUserEmail(userEmail);
        if (payment.isEmpty()) {
            throw new NotFoundException("payment not found");
        }

        payment.get().setAmount(00.00);
        paymentRepository.save(payment.get());
        return ResponseEntity.ok().build();
    }
}
