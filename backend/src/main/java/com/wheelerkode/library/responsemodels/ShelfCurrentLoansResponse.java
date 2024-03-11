package com.wheelerkode.library.responsemodels;

import com.wheelerkode.library.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ShelfCurrentLoansResponse {

    private Book book;
    private int daysLeft;

}
