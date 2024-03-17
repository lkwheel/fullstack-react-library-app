package com.wheelerkode.library.controllers;

import com.wheelerkode.library.entity.Book;
import com.wheelerkode.library.entity.LibraryUser;
import com.wheelerkode.library.responsemodels.ShelfCurrentLoansResponse;
import com.wheelerkode.library.services.BookService;
import com.wheelerkode.library.services.UserDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/books")
@RequiredArgsConstructor
@Log4j2
public class BookController {

    private final UserDataService userDataService;
    private final BookService bookService;

    @GetMapping()
    public ResponseEntity<Page<Book>> getAllBooks(Pageable pageable) {
        Page<Book> books = bookService.getAllBooks(pageable);
        return ResponseEntity.ok().body(books);
    }

    @GetMapping("/protected/current-loans")
    public ResponseEntity<List<ShelfCurrentLoansResponse>> getCurrentLoans() throws Exception {
        ResponseEntity<?> userDataResponse = userDataService.getUserData();
        if (!userDataResponse.getStatusCode().is2xxSuccessful()) {
            log.warn("Problem getting user data");
            return ResponseEntity.badRequest().build();
        }
        LibraryUser user = (LibraryUser) userDataResponse.getBody();
        List<ShelfCurrentLoansResponse> currentLoans = bookService.currentLoans(user.getEmail());
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
    public ResponseEntity<Book> checkoutBook(@RequestParam("bookId") Long bookId) {
        ResponseEntity<?> userDataResponse = userDataService.getUserData();
        if (!userDataResponse.getStatusCode().is2xxSuccessful()) {
            log.warn("Problem getting user data");
            return ResponseEntity.badRequest().build();
        }
        LibraryUser user = (LibraryUser) userDataResponse.getBody();
        try {
            Book checkedOutBook = bookService.checkoutBook(user.getEmail(), bookId);
            return ResponseEntity.ok().body(checkedOutBook);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/protected/current-loans/count")
    public ResponseEntity<Integer> getCurrentLoansCount() {
        ResponseEntity<?> userDataResponse = userDataService.getUserData();
        if (!userDataResponse.getStatusCode().is2xxSuccessful()) {
            log.warn("Problem getting user data");
            return ResponseEntity.badRequest().build();
        }
        LibraryUser user = (LibraryUser) userDataResponse.getBody();
        Integer count = bookService.currentLoansCount(user.getEmail());
        return ResponseEntity.ok().body(count);
    }

    @GetMapping("/protected/is-checked-out/by-user")
    public ResponseEntity<Boolean> isBookCheckedOutByUser(@RequestParam("bookId") Long bookId) {
        ResponseEntity<?> userDataResponse = userDataService.getUserData();
        if (!userDataResponse.getStatusCode().is2xxSuccessful()) {
            log.warn("Problem getting user data");
            return ResponseEntity.badRequest().build();
        }
        LibraryUser user = (LibraryUser) userDataResponse.getBody();
        Boolean isCheckedOut = bookService.checkoutBookByUser(user.getEmail(), bookId);
        return ResponseEntity.ok().body(isCheckedOut);
    }

    @PutMapping("/protected/return")
    public ResponseEntity<Void> returnBook(@RequestParam("bookId") Long bookId) {
        ResponseEntity<?> userDataResponse = userDataService.getUserData();
        if (!userDataResponse.getStatusCode().is2xxSuccessful()) {
            log.warn("Problem getting user data");
            return ResponseEntity.badRequest().build();
        }
        LibraryUser user = (LibraryUser) userDataResponse.getBody();
        try {
            bookService.returnBook(user.getEmail(), bookId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/protected/renew-loan")
    public ResponseEntity<Void> renewBookLoan(@RequestParam("bookId") Long bookId) {
        ResponseEntity<?> userDataResponse = userDataService.getUserData();
        if (!userDataResponse.getStatusCode().is2xxSuccessful()) {
            log.warn("Problem getting user data");
            return ResponseEntity.badRequest().build();
        }
        LibraryUser user = (LibraryUser) userDataResponse.getBody();
        try {
            bookService.renewLoan(user.getEmail(), bookId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
