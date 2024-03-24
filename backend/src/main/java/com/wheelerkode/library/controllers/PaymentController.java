package com.wheelerkode.library.controllers;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.wheelerkode.library.entity.LibraryUser;
import com.wheelerkode.library.entity.Payment;
import com.wheelerkode.library.models.NotFoundException;
import com.wheelerkode.library.requestmodels.PaymentInfoRequest;
import com.wheelerkode.library.services.PaymentService;
import com.wheelerkode.library.services.UserDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value = "/api/payment/protected")
@RequiredArgsConstructor
@Log4j2
public class PaymentController {

    private final UserDataService userDataService;
    private final PaymentService paymentService;

    @GetMapping()
    public ResponseEntity<Payment> getPayment() {
        ResponseEntity<?> userDataResponse = userDataService.getUserData();
        if (!userDataResponse.getStatusCode().is2xxSuccessful()) {
            log.warn("Problem getting user data");
            return ResponseEntity.badRequest().build();
        }
        LibraryUser user = (LibraryUser) userDataResponse.getBody();
        Optional<Payment> paymentByUserEmail = paymentService.getPaymentByUserEmail(user.getEmail());
        if (paymentByUserEmail.isEmpty()) {
            throw new NotFoundException("Payment not found");
        }
        return ResponseEntity.ok(paymentByUserEmail.get());
    }

    @PostMapping("/payment-intent")
    public ResponseEntity<String> createPaymentIntent(@RequestBody PaymentInfoRequest paymentInfoRequest)
            throws StripeException {
        ResponseEntity<?> userDataResponse = userDataService.getUserData();
        if (!userDataResponse.getStatusCode().is2xxSuccessful()) {
            log.warn("Problem getting user data");
            return ResponseEntity.badRequest().build();
        }

        PaymentIntent paymentIntent = paymentService.createPaymentIntent(paymentInfoRequest);
        String paymentString = paymentIntent.toJson();

        return ResponseEntity.ok(paymentString);
    }

    @PutMapping("/payment-complete")
    public ResponseEntity<String> stripePaymentComplete() {
        ResponseEntity<?> userDataResponse = userDataService.getUserData();
        if (!userDataResponse.getStatusCode().is2xxSuccessful()) {
            log.warn("Problem getting user data");
            return ResponseEntity.badRequest().build();
        }
        LibraryUser user = (LibraryUser) userDataResponse.getBody();
        try {
            return paymentService.stripePayment(user.getEmail());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
