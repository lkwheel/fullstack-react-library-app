package com.wheelerkode.library.dao;

import com.wheelerkode.library.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findBooksByUserEmail(String userEmail);

}
