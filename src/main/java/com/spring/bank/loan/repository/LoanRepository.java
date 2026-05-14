package com.spring.bank.loan.repository;

import com.spring.bank.loan.enums.LoanStatus;
import com.spring.bank.loan.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByAccountId(Long accountId);

    List<Loan> findByStatus(LoanStatus status);
}
