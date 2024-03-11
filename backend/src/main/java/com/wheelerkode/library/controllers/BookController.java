package com.wheelerkode.library.controllers;

import com.wheelerkode.library.entity.Book;
import com.wheelerkode.library.responsemodels.ShelfCurrentLoansResponse;
import com.wheelerkode.library.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping()
    public ResponseEntity<Page<Book>> getAllBooks(Pageable pageable) {
        Page<Book> books = bookService.getAllBooks(pageable);
        return ResponseEntity.ok().body(books);
    }

    @GetMapping("/protected/current-loans")
    public ResponseEntity<List<ShelfCurrentLoansResponse>> getCurrentLoans(@RequestParam("userEmail") String userEmail)
            throws Exception {
        List<ShelfCurrentLoansResponse> currentLoans = bookService.currentLoans(userEmail);
        return ResponseEntity.ok().body(currentLoans);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<Book> getBook(@PathVariable Long bookId) {
        Optional<Book> bookById = bookService.getBookById(bookId);
        return bookById.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/find-by-title-containing")
    public ResponseEntity<Page<Book>> getBooksByTitleContaining(@RequestParam("title") String title,
                                                                Pageable pageable) {
        Page<Book> books = bookService.getBooksByTitleContaining(title, pageable);
        return ResponseEntity.ok().body(books);
    }

    @GetMapping("/find-by-category")
    public ResponseEntity<Page<Book>> getBooksByCategory(@RequestParam("category") String category, Pageable pageable) {
        Page<Book> books = bookService.getBooksByCategory(category, pageable);
        return ResponseEntity.ok().body(books);
    }

    @PutMapping("/protected/checkout")
    public ResponseEntity<Book> checkoutBook(@RequestParam Map<String, String> params) throws Exception {
        Long bookId = Long.valueOf(params.get("bookId"));
        String userEmail = params.get("userEmail");
        Book checkedOutBook = bookService.checkoutBook(userEmail, bookId);
        return ResponseEntity.ok().body(checkedOutBook);
    }

    @GetMapping("/protected/current-loans/count")
    public ResponseEntity<Integer> getCurrentLoansCount(@RequestHeader("Authorization") String token,
                                                        @RequestParam("userEmail") String userEmail) {
        Integer count = bookService.currentLoansCount(userEmail);
        return ResponseEntity.ok().body(count);
    }

    @GetMapping("/protected/is-checked-out/by-user")
    public ResponseEntity<Boolean> isBookCheckedOutByUser(@RequestParam("userEmail") String userEmail,
                                                          @RequestParam("bookId") Long bookId) {
        Boolean isCheckedOut = bookService.checkoutBookByUser(userEmail, bookId);
        return ResponseEntity.ok().body(isCheckedOut);
    }

    @PutMapping("/protected/return")
    public ResponseEntity<Void> returnBook(@RequestParam("userEmail") String userEmail,
                                           @RequestParam("bookId") Long bookId) throws Exception {
        bookService.returnBook(userEmail, bookId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/protected/renew-loan")
    public ResponseEntity<Void> renewBookLoan(@RequestParam("userEmail") String userEmail,
                                              @RequestParam("bookId") Long bookId) throws Exception {
        bookService.renewLoan(userEmail, bookId);
        return ResponseEntity.noContent().build();
    }
}
