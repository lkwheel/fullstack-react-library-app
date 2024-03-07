package com.wheelerkode.library.services;

import com.wheelerkode.library.dao.ReviewRepository;
import com.wheelerkode.library.entity.Review;
import com.wheelerkode.library.reviewRequestModels.ReviewRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Page<Review> getAllReviews(Pageable pageable) throws Exception {
        return reviewRepository.findAll(pageable);
    }

    public Page<Review> getBookById(Long bookId, Pageable pageable) throws Exception {
        return reviewRepository.findByBookId(bookId, pageable);
    }

    public Optional<Review> getReviewById(Long reviewId) throws Exception {
        return reviewRepository.findById(reviewId);
    }

    public Optional<Review> getByUserEmailAndBookId(String userEmail, Long bookId) throws Exception {
        return reviewRepository.findByUserEmailAndBookId(userEmail, bookId);
    }

    public void postReview(String userEmail, ReviewRequest reviewRequest) throws Exception {
        Optional<Review> validateReview = reviewRepository.findByUserEmailAndBookId(userEmail, reviewRequest.getBookId());
        if (validateReview.isPresent()) {
            throw new Exception("Review already created");
        }

        Review review = new Review();
        review.setBookId(reviewRequest.getBookId());
        review.setRating(reviewRequest.getRating());
        review.setUserEmail(userEmail);

        if (reviewRequest.getReviewDescription().isPresent()) {
            review.setReviewDescription(reviewRequest.getReviewDescription().map(Objects::toString).orElse(null));
        }

        review.setDate(Date.from(Instant.now()));
        reviewRepository.save(review);
    }

    public Boolean userReviewListed(String userEmail, Long bookId) {
        Optional<Review> validateReview = reviewRepository.findByUserEmailAndBookId(userEmail, bookId);
        return validateReview.isPresent();
    }
}
