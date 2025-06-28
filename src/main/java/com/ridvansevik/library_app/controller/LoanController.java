package com.ridvansevik.library_app.controller;

import com.ridvansevik.library_app.model.Loan;
import com.ridvansevik.library_app.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/borrow/{bookId}")
    public ResponseEntity<Loan> borrowBook(@PathVariable Long bookId, Principal principal){
        if(principal==null){
            throw new AccessDeniedException("Authentication is required to borrow a book");
        }
        Loan loan = loanService.borrowBook(bookId,principal.getName());
        return ResponseEntity.ok(loan);
    }

    @PostMapping("/return/{bookId}")
    public ResponseEntity<Loan> returnBook(@PathVariable Long bookId,Principal principal){
        if(principal == null){
            throw new AccessDeniedException("Authentication is required to return a book");
        }
        Loan loan = loanService.returnBook(bookId,principal.getName());
        return ResponseEntity.ok(loan);
    }

}
