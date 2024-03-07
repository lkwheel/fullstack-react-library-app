package com.wheelerkode.library.controllers;

import com.wheelerkode.library.entity.Review;
import com.wheelerkode.library.reviewRequestModels.ReviewRequest;
import com.wheelerkode.library.services.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping()
    public Page<Review> getAllReviews(Pageable pageable) throws Exception {
        return reviewService.getAllReviews(pageable);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<Review> getReview(@PathVariable Long reviewId, Pageable pageable) throws Exception {
        Optional<Review> reviewById = reviewService.getReviewById(reviewId);
        return reviewById.map(review -> ResponseEntity.ok().body(review)).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/findByBookId")
    public Page<Review> getBookById(@RequestParam("bookId") Long bookId, Pageable pageable) throws Exception {
        return reviewService.getBookById(bookId, pageable);
    }

    @GetMapping("/findByUserEmailAndBookId")
    public ResponseEntity<Review> getByUserEmailAndBookId(
            @RequestParam("userEmail") String userEmail, @RequestParam("bookId") Long bookId) throws Exception {
        Optional<Review> byUserEmailAndBookId = reviewService.getByUserEmailAndBookId(userEmail, bookId);
        return byUserEmailAndBookId.map(review -> ResponseEntity.ok().body(review)).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/protected")
    public void postReview(
            @RequestParam("userEmail") String userEmail, @RequestBody ReviewRequest reviewRequest) throws Exception {
        reviewService.postReview(userEmail, reviewRequest);
    }

    @GetMapping("/protected/user/book")
    public Boolean reviewBookByUser(@RequestParam("userEmail") String userEmail, @RequestParam("bookId") Long bookId) {
        return reviewService.userReviewListed(userEmail, bookId);
    }
}
