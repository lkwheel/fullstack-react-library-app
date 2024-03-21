package com.wheelerkode.library.dao;

import com.wheelerkode.library.entity.Review;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Page<Review> findByBookId(UUID bookId, Pageable pageable);

    Optional<Review> findByUserEmailAndBookId(String userEmail, UUID bookId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Review r WHERE r.bookId = :bookId")
    void deleteAllByBookId(@Param("bookId") UUID bookId);
}
