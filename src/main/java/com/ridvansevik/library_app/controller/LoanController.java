package com.ridvansevik.library_app.controller;

import com.ridvansevik.library_app.dto.LoanDto;
import com.ridvansevik.library_app.mapper.DtoMapper;
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
    private final DtoMapper dtoMapper;

    @PostMapping("/borrow/{bookId}")
    public ResponseEntity<LoanDto> borrowBook(@PathVariable Long bookId, Principal principal){
        if(principal==null){
            throw new AccessDeniedException("Authentication is required to borrow a book");
        }
        Loan loan = loanService.borrowBook(bookId,principal.getName());
        LoanDto loanDto = dtoMapper.toLoanDto(loan);
        return ResponseEntity.ok(loanDto);
    }

    @PostMapping("/return/{bookId}")
    public ResponseEntity<LoanDto> returnBook(@PathVariable Long bookId,Principal principal){
        if(principal == null){
            throw new AccessDeniedException("Authentication is required to return a book");
        }
        Loan loan = loanService.returnBook(bookId,principal.getName());
        LoanDto loanDto = dtoMapper.toLoanDto(loan);
        return ResponseEntity.ok(loanDto);
    }

}
