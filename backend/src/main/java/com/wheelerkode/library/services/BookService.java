package com.wheelerkode.library.services;

import com.wheelerkode.library.dao.BookRepository;
import com.wheelerkode.library.dao.CheckoutRepository;
import com.wheelerkode.library.dao.HistoryRepository;
import com.wheelerkode.library.dao.PaymentRepository;
import com.wheelerkode.library.entity.Book;
import com.wheelerkode.library.entity.Checkout;
import com.wheelerkode.library.entity.History;
import com.wheelerkode.library.entity.Payment;
import com.wheelerkode.library.models.NotFoundException;
import com.wheelerkode.library.models.OutstandingFeesException;
import com.wheelerkode.library.models.ProcessingException;
import com.wheelerkode.library.responsemodels.ShelfCurrentLoansResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class BookService {

    public static final double LATE_FEE = 1.0;
    private final BookRepository bookRepository;
    private final CheckoutRepository checkoutRepository;
    private final HistoryRepository historyRepository;
    private final PaymentRepository paymentRepository;

    public Book checkoutBook(String userEmail, UUID bookId) {
        Optional<Book> book = bookRepository.findById(bookId);
        Checkout validateBookCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);

        if (book.isEmpty() || validateBookCheckout != null || book.get().getCopiesAvailable() <= 0) {
            throw new NotFoundException("Book doesn't exist or is already checked out by user");
        }

        List<Checkout> currentBooksCheckedOut = checkoutRepository.findBooksByUserEmail(userEmail);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        boolean booksNeededReturn = false;
        for (Checkout checkout : currentBooksCheckedOut) {
            try {
                Date d1 = sdf.parse(checkout.getReturnDate());
                Date d2 = sdf.parse(LocalDate.now().toString());
                TimeUnit time = TimeUnit.DAYS;
                double differenceInTime = time.convert(d1.getTime() - d2.getTime(), TimeUnit.MILLISECONDS);
                if (differenceInTime < 0) {
                    booksNeededReturn = true;
                    break;
                }
            } catch (ParseException e) {
                throw new ProcessingException("Server processing exception", e);
            }
        }

        Optional<Payment> userPayment = paymentRepository.findPaymentByUserEmail(userEmail);
        if ((userPayment.isPresent() && userPayment.get()
                                                   .getAmount() > 0) || (userPayment.isPresent() && booksNeededReturn)) {
            throw new OutstandingFeesException("Outstanding fees");
        }

        if (userPayment.isEmpty()) {
            Payment payment = new Payment();
            payment.setAmount(00.00);
            payment.setUserEmail(userEmail);
            paymentRepository.save(payment);
        }

        book.get().setCopiesAvailable(book.get().getCopiesAvailable() - 1);
        bookRepository.save(book.get());

        Checkout checkout = new Checkout(userEmail, LocalDate.now().toString(), LocalDate.now().plusDays(7).toString(),
                                         book.get().getId());

        checkoutRepository.save(checkout);

        return book.get();
    }

    public Boolean checkoutBookByUser(String userEmail, UUID bookId) {
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        return validateCheckout != null;
    }

    public Integer currentLoansCount(String userEmail) {
        return checkoutRepository.findBooksByUserEmail(userEmail).size();
    }

    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Optional<Book> getBookById(UUID bookId) {
        return bookRepository.findById(bookId);
    }

    public Page<Book> getBooksByTitleContaining(String title, Pageable pageable) {
        return bookRepository.findByTitleContaining(title, pageable);
    }

    public Page<Book> getBooksByCategory(String category, Pageable pageable) {
        return bookRepository.findByCategory(category, pageable);
    }

    public List<ShelfCurrentLoansResponse> currentLoans(String userEmail) {
        List<ShelfCurrentLoansResponse> shelfCurrentLoansResponses = new ArrayList<>();
        List<Checkout> checkoutList = checkoutRepository.findBooksByUserEmail(userEmail);
        List<UUID> bookIdList = new ArrayList<>();

        for (Checkout checkout : checkoutList) {
            bookIdList.add(checkout.getBookId());
        }

        List<Book> books = bookRepository.findBooksByBookIs(bookIdList);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Book book : books) {
            Optional<Checkout> checkout = checkoutList.stream()
                                                      .filter(x -> Objects.equals(x.getBookId(), book.getId()))
                                                      .findFirst();

            if (checkout.isPresent()) {
                Date d1;
                int differenceInDays;
                try {
                    d1 = sdf.parse(checkout.get().getReturnDate());
                    Date d2 = sdf.parse(LocalDate.now().toString());

                    TimeUnit time = TimeUnit.HOURS;

                    long differenceInHours = time.convert(d1.getTime() - d2.getTime(), TimeUnit.MILLISECONDS);
                    differenceInDays = (int) Math.ceil((double) differenceInHours / 24);
                } catch (ParseException e) {
                    throw new ProcessingException("Server processing exception", e);
                }

                shelfCurrentLoansResponses.add(new ShelfCurrentLoansResponse(book, differenceInDays));
            }

        }

        return shelfCurrentLoansResponses;
    }

    public void returnBook(String userEmail, UUID bookId) {
        Optional<Book> book = bookRepository.findById(bookId);
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        if (book.isEmpty() || validateCheckout == null) {
            throw new NotFoundException("Book does not exist or not checked out by user");
        }

        book.get().setCopiesAvailable(book.get().getCopiesAvailable() + 1);

        bookRepository.save(book.get());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d1 = sdf.parse(validateCheckout.getReturnDate());
            Date d2 = sdf.parse(LocalDate.now().toString());
            TimeUnit time = TimeUnit.DAYS;

            long differenceInTime = time.convert(d1.getTime() - d2.getTime(), TimeUnit.MILLISECONDS);

            if (differenceInTime < 0) {
                Optional<Payment> foundPayment = paymentRepository.findPaymentByUserEmail(userEmail);
                Payment payment;
                payment = foundPayment.orElseGet(Payment::new);
                payment.setAmount(payment.getAmount() + (differenceInTime * (LATE_FEE * -1)));
                paymentRepository.save(payment);
            }
        } catch (ParseException e) {
            throw new ProcessingException("Server processing exception", e);
        }

        checkoutRepository.deleteById(validateCheckout.getId());

        History history = new History(book.get().getTitle(), book.get().getAuthor(), book.get().getDescription(),
                                      book.get().getImg(), userEmail, validateCheckout.getCheckoutDate(),
                                      LocalDate.now().toString());

        historyRepository.save(history);
    }

    public void renewLoan(String userEmail, UUID bookId) {
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);

        if (validateCheckout == null) {
            throw new NotFoundException("Book does not exist or not checked out by user");
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Date d1 = sdf.parse(validateCheckout.getReturnDate());
            Date d2 = sdf.parse(LocalDate.now().toString());

            if (d1.compareTo(d2) > 0 || d1.compareTo(d2) == 0) {
                validateCheckout.setReturnDate(LocalDate.now().plusDays(7).toString());
                checkoutRepository.save(validateCheckout);
            }
        } catch (ParseException e) {
            throw new ProcessingException("Server processing exception", e);
        }
    }
}
