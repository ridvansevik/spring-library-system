package com.ridvansevik.library_app.repository;

import com.ridvansevik.library_app.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan,Long> {

Optional<Loan> findByBookIdAndReturnDateIsNull(Long bookId);
    List<Loan> findByReturnDateIsNull();

}
