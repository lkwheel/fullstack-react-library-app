package com.wheelerkode.library.dao;

import com.wheelerkode.library.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Page<Review> findByBookId(UUID bookId, Pageable pageable);

    Optional<Review> findByUserEmailAndBookId(String userEmail, UUID bookId);
}
