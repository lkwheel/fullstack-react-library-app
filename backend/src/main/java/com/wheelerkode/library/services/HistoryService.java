package com.wheelerkode.library.services;

import com.wheelerkode.library.dao.BookRepository;
import com.wheelerkode.library.dao.CheckoutRepository;
import com.wheelerkode.library.dao.HistoryRepository;
import com.wheelerkode.library.entity.Book;
import com.wheelerkode.library.entity.Checkout;
import com.wheelerkode.library.entity.History;
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
public class HistoryService {

    private final HistoryRepository historyRepository;

    public Page<History> getHistoryByUserEmail(String userEmail, Pageable pageable) {
        return historyRepository.findByUserEmail(userEmail, pageable);
    }
}
