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

    @GetMapping("/protected/currentloans")
    public List<ShelfCurrentLoansResponse> currentLoans(@RequestParam("userEmail") String userEmail) throws Exception {
        return bookService.currentLoans(userEmail);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<Book> getBook(@PathVariable Long bookId) {
        Optional<Book> bookById = bookService.getBookById(bookId);
        return bookById.map(book -> ResponseEntity.ok().body(book)).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("findByTitleContaining")
    public Page<Book> getBooksByTitleContaining(@RequestParam("title") String title, Pageable pageable) {
        return bookService.getBooksByTitleContaining(title, pageable);
    }

    @GetMapping("findByCategory")
    public Page<Book> getBooksByCategory(@RequestParam("category") String category, Pageable pageable) {
        return bookService.getBooksByCategory(category, pageable);
    }

    @PutMapping("/protected/checkout")
    public Book checkoutBook(
            @RequestParam("bookId") Long bookId, @RequestParam("userEmail") String userEmail) throws Exception {
        return bookService.checkoutBook(userEmail, bookId);
    }

    @GetMapping("/protected/currentloans/count")
    public Integer currentLoansCount(@RequestParam("userEmail") String userEmail) {
        return bookService.currentLoansCount(userEmail);
    }

    @GetMapping("/protected/ischeckedout/byuser")
    public Boolean checkoutBookByUser(
            @RequestParam("userEmail") String userEmail, @RequestParam("bookId") Long bookId) {
        return bookService.checkoutBookByUser(userEmail, bookId);
    }

    @PutMapping("protected/return")
    public void returnBook(
            @RequestParam("userEmail") String userEmail, @RequestParam("bookId") Long bookId) throws Exception {
        bookService.returnBook(userEmail, bookId);
    }

    @PutMapping("protected/renew/loan")
    public void renewBook(
            @RequestParam("userEmail") String userEmail, @RequestParam("bookId") Long bookId) throws Exception {
        bookService.renewLoan(userEmail, bookId);
    }
}
