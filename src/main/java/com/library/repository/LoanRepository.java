
package com.library.repository;

import com.library.model.Loan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    // Count books that are currently issued
    long countByReturnDateIsNull();

    // Find all active loans
    List<Loan> findByReturnDateIsNull();

    // Find all returned books
    List<Loan> findByReturnDateIsNotNull();

    // Find overdue books
    List<Loan> findByReturnDateIsNullAndDueDateBefore(
            LocalDate date);

    // Check whether a book has an active loan
    boolean existsByBookIdAndReturnDateIsNull(Long bookId);

    // Check whether a book has any loan history
    boolean existsByBookId(Long bookId);

    // Check whether a member has any loan history
    boolean existsByMemberId(Long memberId);

    // Retrieve all loans, newest first
    List<Loan> findAllByOrderByIssueDateDescIdDesc();

    // Retrieve five recent loans for the dashboard
    List<Loan> findTop5ByOrderByIssueDateDescIdDesc();
}
