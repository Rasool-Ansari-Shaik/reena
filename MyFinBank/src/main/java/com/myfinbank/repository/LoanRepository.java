package com.myfinbank.repository;

import com.myfinbank.entity.Account;
import com.myfinbank.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    Optional<Loan> findByLoanAccountNumber(String loanAccountNumber);
}
