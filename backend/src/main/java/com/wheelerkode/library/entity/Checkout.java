package com.wheelerkode.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "checkout")
@Data
@NoArgsConstructor
public class Checkout {
    @Id @GeneratedValue
    @JdbcTypeCode(Types.VARCHAR)
    private UUID id;
    @Column(name = "user_email")
    private String userEmail;
    @Column(name = "checkout_date")
    private String checkoutDate;
    @Column(name = "return_date")
    private String returnDate;
    @Column(name = "book_id")
    private UUID bookId;

    public Checkout(String userEmail, String checkoutDate, String returnDate, UUID bookId) {
        this.userEmail = userEmail;
        this.checkoutDate = checkoutDate;
        this.returnDate = returnDate;
        this.bookId = bookId;
    }
}
