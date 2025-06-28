package com.ridvansevik.library_app.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id",nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    private LocalDate loanDate;

    private LocalDate dueDate;

    private LocalDate returnDate;

    private BigDecimal fineAmount;

    public Loan(Book book,User user){
        this.book=book;
        this.user=user;
        this.loanDate=LocalDate.now();
        this.dueDate=LocalDate.now().plusWeeks(2);
    }
}
