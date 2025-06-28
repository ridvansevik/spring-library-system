package com.ridvansevik.library_app.service;


import com.ridvansevik.library_app.exception.ActiveLoanNotFoundException;
import com.ridvansevik.library_app.exception.BookAlreadyBorrowedException;
import com.ridvansevik.library_app.exception.ResourceNotFoundException;
import com.ridvansevik.library_app.exception.UserHasOverdueBooksOrUnpaidFinesException;
import com.ridvansevik.library_app.model.*;
import com.ridvansevik.library_app.repository.BookRepository;
import com.ridvansevik.library_app.repository.LoanRepository;
import com.ridvansevik.library_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private static final BigDecimal DAILY_FINE_RATE = new BigDecimal("1.00");
    @Transactional
    public Loan borrowBook(Long bookId,String username){
        if (loanRepository.existsByUser_UsernameAndReturnDateIsNullAndDueDateBefore(username, LocalDate.now()) ||
                loanRepository.existsByUser_UsernameAndFineAmountGreaterThan(username, BigDecimal.ZERO)) {
            throw new UserHasOverdueBooksOrUnpaidFinesException(username);
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Kitap Bulunamadı" + bookId));


        if(book.getBookStatus() == BookStatus.BORROWED){
            throw new BookAlreadyBorrowedException(bookId);
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı Bulunamadı " + username));


        book.setBookStatus(BookStatus.BORROWED);
        bookRepository.save(book);

        Loan loan = new Loan(book,user);
        return loanRepository.save(loan);
    }

    @Transactional
    public Loan returnBook(Long bookId,String username){

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Bu Kullanıcı Bulunamadı " + username ));


        Loan loan = loanRepository.findByBookIdAndReturnDateIsNull(bookId)
                .orElseThrow(() -> new ActiveLoanNotFoundException(bookId));


        boolean isBorrower = loan.getUser().getUsername().equals(username);
        boolean isAdmin = user.getRole().equals(Role.ROLE_ADMIN);

        if(!isBorrower && !isAdmin){
            throw new AccessDeniedException("Bu kitabı iade etme yetkiniz yok.");
        }


        Book book = loan.getBook();
        book.setBookStatus(BookStatus.AVAILABLE);
        bookRepository.save(book);

        loan.setReturnDate(LocalDate.now());

        if (loan.getReturnDate().isAfter(loan.getDueDate())) {
            long overdueDays = ChronoUnit.DAYS.between(loan.getDueDate(), loan.getReturnDate());
            BigDecimal fine = DAILY_FINE_RATE.multiply(new BigDecimal(overdueDays));
            loan.setFineAmount(fine);
        }

        return loanRepository.save(loan);
    }

    public List<Loan> findActiveLoans() {
        return loanRepository.findByReturnDateIsNull();
    }

    public List<Loan> findLoansByUsername(String username) { return loanRepository.findByUser_Username(username); }
}
