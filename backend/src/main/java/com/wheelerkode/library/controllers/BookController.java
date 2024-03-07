package com.wheelerkode.library.controllers;

import com.wheelerkode.library.entity.Book;
import com.wheelerkode.library.services.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping()
    public Page<Book> getAllBooks(Pageable pageable) throws Exception {
        return bookService.getAllBooks(pageable);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<Book> getBook(@PathVariable Long bookId, Pageable pageable) throws Exception {
        Optional<Book> bookById = bookService.getBookById(bookId);
        return bookById.map(book -> ResponseEntity.ok().body(book)).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("findByTitleContaining")
    public Page<Book> getBooksByTitleContaining(@RequestParam("title") String title, Pageable pageable) throws Exception {
        return bookService.getBooksByTitleContaining(title, pageable);
    }

    @GetMapping("findByCategory")
    public Page<Book> getBooksByCategory(@RequestParam("category") String category, Pageable pageable) throws Exception {
        return bookService.getBooksByCategory(category, pageable);
    }

    @PutMapping("/protected/checkout")
    public Book checkoutBook(@RequestParam("bookId") Long bookId, @RequestParam("userEmail") String userEmail) throws Exception {
        return bookService.checkoutBook(userEmail, bookId);
    }

    @GetMapping("/protected/currentloans/count")
    public Integer currentLoansCount(@RequestParam("userEmail") String userEmail) throws Exception {
        return bookService.currentLoansCount(userEmail);
    }

    @GetMapping("/protected/ischeckedout/byuser")
    public Boolean checkoutBookByUser(@RequestParam("userEmail") String userEmail, @RequestParam("bookId") Long bookId) throws Exception {
        return bookService.checkoutBookByUser(userEmail, bookId);
    }
}
