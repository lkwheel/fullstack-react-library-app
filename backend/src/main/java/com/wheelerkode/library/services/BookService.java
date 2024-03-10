package com.wheelerkode.library.services;

import com.wheelerkode.library.dao.BookRepository;
import com.wheelerkode.library.dao.CheckoutRepository;
import com.wheelerkode.library.entity.Book;
import com.wheelerkode.library.entity.Checkout;
import com.wheelerkode.library.responsemodels.ShelfCurrentLoansResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final CheckoutRepository checkoutRepository;

    public Book checkoutBook(String userEmail, Long bookId) throws Exception {
        Optional<Book> book = bookRepository.findById(bookId);
        Checkout validateBookCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);

        if (book.isEmpty() || validateBookCheckout != null || book.get().getCopiesAvailable() <= 0) {
            throw new Exception("Book doesn't exist or is already checked out by user");
        }

        book.get().setCopiesAvailable(book.get().getCopiesAvailable() - 1);
        bookRepository.save(book.get());

        Checkout checkout = new Checkout(userEmail, LocalDate.now().toString(), LocalDate.now().plusDays(7).toString(), book.get().getId());

        checkoutRepository.save(checkout);

        return book.get();
    }

    public Boolean checkoutBookByUser(String userEmail, Long bookId) {
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        return validateCheckout != null;
    }

    public Integer currentLoansCount(String userEmail) {
        return checkoutRepository.findBooksByUserEmail(userEmail).size();
    }

    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Optional<Book> getBookById(Long bookId) {
        return bookRepository.findById(bookId);
    }

    public Page<Book> getBooksByTitleContaining(String title, Pageable pageable) {
        return bookRepository.findByTitleContaining(title, pageable);
    }

    public Page<Book> getBooksByCategory(String category, Pageable pageable) {
        return bookRepository.findByCategory(category, pageable);
    }

    public List<ShelfCurrentLoansResponse> currentLoans(String userEmail) throws Exception {
        List<ShelfCurrentLoansResponse> shelfCurrentLoansResponses = new ArrayList<>();
        List<Checkout> checkoutList = checkoutRepository.findBooksByUserEmail(userEmail);
        List<Long> bookIdList = new ArrayList<>();

        for (Checkout checkout : checkoutList) {
            bookIdList.add(checkout.getBookId());
        }

        List<Book> books = bookRepository.findBooksByBookIs(bookIdList);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Book book : books) {
            Optional<Checkout> checkout = checkoutList.stream().filter(x -> Objects.equals(x.getBookId(), book.getId())).findFirst();

            if (checkout.isPresent()) {
                Date d1 = sdf.parse(checkout.get().getReturnDate());
                Date d2 = sdf.parse(LocalDate.now().toString());

                TimeUnit time = TimeUnit.HOURS;

                long differenceInHours = time.convert(d1.getTime() - d2.getTime(), TimeUnit.MILLISECONDS);
                int differenceInDays = (int) Math.ceil((double) differenceInHours / 24);

                shelfCurrentLoansResponses.add(new ShelfCurrentLoansResponse(book, differenceInDays));
            }

        }

        return shelfCurrentLoansResponses;
    }

    public void returnBook(String userEmail, Long bookId) throws Exception {
        Optional<Book> book = bookRepository.findById(bookId);
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        if (book.isEmpty() || validateCheckout == null) {
            throw new Exception("Book does not exist or not checked out by user");
        }

        book.get().setCopiesAvailable(book.get().getCopiesAvailable() + 1);
        bookRepository.save(book.get());
        checkoutRepository.deleteById(validateCheckout.getId());
    }

    public void renewLoan(String userEmail, Long bookId) throws Exception {
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);

        if (validateCheckout == null) {
            throw new Exception("Book does not exist or not checked out by user");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date d1 = sdf.parse(validateCheckout.getReturnDate());
        Date d2 = sdf.parse(LocalDate.now().toString());

        if (d1.compareTo(d2) > 0 || d1.compareTo(d2) == 0) {
            validateCheckout.setReturnDate(LocalDate.now().plusDays(7).toString());
            checkoutRepository.save(validateCheckout);
        }
    }
}
