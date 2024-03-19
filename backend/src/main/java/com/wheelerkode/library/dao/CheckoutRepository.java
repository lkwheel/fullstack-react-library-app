package com.wheelerkode.library.dao;

import com.wheelerkode.library.entity.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CheckoutRepository extends JpaRepository<Checkout, UUID> {

    Checkout findByUserEmailAndBookId(String userEmail, UUID bookId);

    List<Checkout> findBooksByUserEmail(String userEmail);
}
