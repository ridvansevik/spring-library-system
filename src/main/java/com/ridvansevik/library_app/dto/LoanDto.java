package com.ridvansevik.library_app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanDto(
        Long id,
        BookDto book,
        UserDto user,
        LocalDate loanDate,
        LocalDate dueDate,
        LocalDate returnDate,
        BigDecimal fineAmount
) {}