package com.ridvansevik.library_app.service;


import com.ridvansevik.library_app.exception.ResourceNotFoundException;
import com.ridvansevik.library_app.model.*;
import com.ridvansevik.library_app.repository.BookRepository;
import com.ridvansevik.library_app.repository.LoanRepository;
import com.ridvansevik.library_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;

    @Transactional
    public Loan borrowBook(Long bookId,String username){
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Kitap Bulunamadı" + bookId));


        if(book.getBookStatus() == BookStatus.BORROWED){
            throw new IllegalStateException("Bu kitap zaten alınmış");
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
                .orElseThrow(() -> new IllegalStateException("Bu Kitap İçin Aktif Bir Ödünç Alma Bulunamadı " + bookId));


        boolean isBorrower = loan.getUser().getUsername().equals(username);
        boolean isAdmin = user.getRole().equals(Role.ROLE_ADMIN);

        if(!isBorrower && !isAdmin){
            throw new AccessDeniedException("Bu kitabı iade etme yetkiniz yok.");
        }


        Book book = loan.getBook();
        book.setBookStatus(BookStatus.AVAILABLE);
        bookRepository.save(book);

        loan.setReturnDate(LocalDate.now());
        return loanRepository.save(loan);
    }

    public List<Loan> findActiveLoans() {
        return loanRepository.findByReturnDateIsNull();
    }
}
