package com.ridvansevik.library_app.repository;

import com.ridvansevik.library_app.model.Loan;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {


    @EntityGraph(attributePaths = {"book", "user"})
    Optional<Loan> findByBookIdAndReturnDateIsNull(Long bookId);

    @EntityGraph(attributePaths = {"book", "user"})
    List<Loan> findByReturnDateIsNull();

    boolean existsByBookId(Long bookId);

    @EntityGraph(attributePaths = {"book", "user"})
    List<Loan> findByUser_Username(String username);
}