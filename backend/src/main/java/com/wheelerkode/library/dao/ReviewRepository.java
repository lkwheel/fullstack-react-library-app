package com.wheelerkode.library.dao;

import com.wheelerkode.library.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByBookId(Long bookId, Pageable pageable);

    Optional<Review> findByUserEmailAndBookId(String userEmail, Long bookId);
}
