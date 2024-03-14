package com.wheelerkode.library.controllers;

import com.wheelerkode.library.entity.Review;
import com.wheelerkode.library.requestmodels.ReviewRequest;
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
    public ResponseEntity<Page<Review>> getAllReviews(Pageable pageable) {
        Page<Review> reviews = reviewService.getAllReviews(pageable);
        return ResponseEntity.ok().body(reviews);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<Review> getReview(@PathVariable Long reviewId, Pageable pageable) {
        Optional<Review> reviewById = reviewService.getReviewById(reviewId);
        return reviewById.map(review -> ResponseEntity.ok().body(review))
                         .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/find-by-book-id")
    public ResponseEntity<Page<Review>> getBookById(@RequestParam("bookId") Long bookId, Pageable pageable) {
        Page<Review> reviews = reviewService.getBookById(bookId, pageable);
        return ResponseEntity.ok().body(reviews);
    }

    @GetMapping("/find-by-user-email-and-book-id")
    public ResponseEntity<Review> getByUserEmailAndBookId(@RequestParam("userEmail") String userEmail,
                                                          @RequestParam("bookId") Long bookId) {
        Optional<Review> byUserEmailAndBookId = reviewService.getByUserEmailAndBookId(userEmail, bookId);
        return byUserEmailAndBookId.map(review -> ResponseEntity.ok().body(review))
                                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/protected")
    public ResponseEntity<Void> postReview(@RequestParam("userEmail") String userEmail,
                                           @RequestBody ReviewRequest reviewRequest) throws Exception {
        reviewService.postReview(userEmail, reviewRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/protected/user/book")
    public ResponseEntity<Boolean> reviewBookByUser(@RequestParam("userEmail") String userEmail,
                                                    @RequestParam("bookId") Long bookId) {
        return ResponseEntity.ok().body(reviewService.userReviewListed(userEmail, bookId));
    }
}
