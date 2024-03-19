package com.wheelerkode.library.controllers;

import com.wheelerkode.library.entity.LibraryUser;
import com.wheelerkode.library.entity.Review;
import com.wheelerkode.library.requestmodels.ReviewRequest;
import com.wheelerkode.library.services.ReviewService;
import com.wheelerkode.library.services.UserDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/reviews")
@RequiredArgsConstructor
@Log4j2
public class ReviewController {

    private final UserDataService userDataService;
    private final ReviewService reviewService;

    @GetMapping()
    public ResponseEntity<Page<Review>> getAllReviews(Pageable pageable) {
        Page<Review> reviews = reviewService.getAllReviews(pageable);
        return ResponseEntity.ok().body(reviews);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<Review> getReview(@PathVariable String reviewId, Pageable pageable) {
        Optional<Review> reviewById = reviewService.getReviewById(UUID.fromString(reviewId));
        return reviewById.map(review -> ResponseEntity.ok().body(review))
                         .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/find-by-book-id")
    public ResponseEntity<Page<Review>> getBookById(@RequestParam("bookId") String bookId, Pageable pageable) {
        Page<Review> reviews = reviewService.getBookById(UUID.fromString(bookId), pageable);
        return ResponseEntity.ok().body(reviews);
    }

    @GetMapping("/find-by-user-email-and-book-id")
    public ResponseEntity<Review> getByUserEmailAndBookId(@RequestParam("bookId") String bookId) {
        ResponseEntity<?> userDataResponse = userDataService.getUserData();
        if (!userDataResponse.getStatusCode().is2xxSuccessful()) {
            log.warn("Problem getting user data");
            return ResponseEntity.badRequest().build();
        }
        LibraryUser user = (LibraryUser) userDataResponse.getBody();
        Optional<Review> byUserEmailAndBookId = reviewService.getByUserEmailAndBookId(user.getEmail(),
                                                                                      UUID.fromString(bookId));
        return byUserEmailAndBookId.map(review -> ResponseEntity.ok().body(review))
                                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/protected")
    public ResponseEntity<Void> postReview(@RequestBody ReviewRequest reviewRequest) {
        ResponseEntity<?> userDataResponse = userDataService.getUserData();
        if (!userDataResponse.getStatusCode().is2xxSuccessful()) {
            log.warn("Problem getting user data");
            return ResponseEntity.badRequest().build();
        }
        LibraryUser user = (LibraryUser) userDataResponse.getBody();

        try {
            reviewService.postReview(user.getEmail(), reviewRequest);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/protected/user/book")
    public ResponseEntity<Boolean> reviewBookByUser(@RequestParam("bookId") UUID bookId) {
        ResponseEntity<?> userDataResponse = userDataService.getUserData();
        if (!userDataResponse.getStatusCode().is2xxSuccessful()) {
            log.warn("Problem getting user data");
            return ResponseEntity.badRequest().build();
        }
        LibraryUser user = (LibraryUser) userDataResponse.getBody();

        return ResponseEntity.ok().body(reviewService.userReviewListed(user.getEmail(), bookId));
    }
}
