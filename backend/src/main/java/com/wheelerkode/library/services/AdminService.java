package com.wheelerkode.library.services;

import com.wheelerkode.library.dao.BookRepository;
import com.wheelerkode.library.dao.CheckoutRepository;
import com.wheelerkode.library.dao.ReviewRepository;
import com.wheelerkode.library.entity.Book;
import com.wheelerkode.library.models.NotFoundException;
import com.wheelerkode.library.requestmodels.AddBookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {
    private final BookRepository bookRepository;
    private final CheckoutRepository checkoutRepository;
    private final ReviewRepository reviewRepository;

    public void increaseBookQuantity(UUID bookId) throws NotFoundException {
        Optional<Book> book = bookRepository.findById(bookId);
        if (!book.isPresent()) {
            throw new NotFoundException("Book not found");
        }

        book.get().setCopiesAvailable(book.get().getCopiesAvailable() + 1);
        book.get().setCopies((book.get().getCopies() + 1));

        bookRepository.save(book.get());
    }

    public void decreaseBookQuantity(UUID bookId) throws NotFoundException {
        Optional<Book> book = bookRepository.findById(bookId);
        if (!book.isPresent() || book.get().getCopiesAvailable() <= 0 || book.get().getCopies() <= 0) {
            throw new NotFoundException("Book not found or quantity locked");
        }

        book.get().setCopiesAvailable(book.get().getCopiesAvailable() - 1);
        book.get().setCopies((book.get().getCopies() - 1));

        bookRepository.save(book.get());
    }

    public void postBook(AddBookRequest addBookRequest) {
        Book book = new Book();
        book.setTitle(addBookRequest.getTitle());
        book.setAuthor(addBookRequest.getAuthor());
        book.setDescription(addBookRequest.getDescription());
        book.setCopies(addBookRequest.getCopies());
        book.setCopiesAvailable(addBookRequest.getCopies());
        book.setCategory(addBookRequest.getCategory());
        book.setImg(addBookRequest.getImg());
        bookRepository.save(book);
    }

    public void deleteBook(UUID bookId) throws NotFoundException {
        Optional<Book> book = bookRepository.findById(bookId);
        if (!book.isPresent()) {
            throw new NotFoundException("Book not found");
        }

        bookRepository.deleteById(bookId);
        checkoutRepository.deleteAllByBookId(bookId);
        reviewRepository.deleteAllByBookId(bookId);
    }
}
