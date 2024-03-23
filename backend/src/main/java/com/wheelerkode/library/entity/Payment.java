package com.wheelerkode.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "payment")
@Data
public class Payment {

    @Id
    @GeneratedValue
    @JdbcTypeCode(Types.VARCHAR)
    private UUID id;
    @Column(name = "user_email")
    private String userEmail;
    @Column(name = "amount")
    private double amount;
}
