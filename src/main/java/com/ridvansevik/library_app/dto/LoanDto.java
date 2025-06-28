package com.ridvansevik.library_app.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanDto {
    private Long id;
    private BookDto book;
    private UserDto user;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private BigDecimal fineAmount;
}