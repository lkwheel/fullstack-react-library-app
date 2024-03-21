package com.wheelerkode.library.dao;

import com.wheelerkode.library.entity.Checkout;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CheckoutRepository extends JpaRepository<Checkout, UUID> {

    Checkout findByUserEmailAndBookId(String userEmail, UUID bookId);

    List<Checkout> findBooksByUserEmail(String userEmail);

    @Modifying
    @Transactional
    @Query("DELETE FROM Checkout c WHERE c.bookId = :bookId")
    void deleteAllByBookId(@Param("bookId") UUID bookId);
}
