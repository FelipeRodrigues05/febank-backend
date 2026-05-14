package com.spring.bank.loan.repository;

import com.spring.bank.loan.model.LoanInstallment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {

    List<LoanInstallment> findByLoanIdOrderByInstallmentNumber(Long loanId);

    List<LoanInstallment> findByLoanIdAndPaidFalse(Long loanId);

    Optional<LoanInstallment> findByLoanIdAndInstallmentNumber(Long loanId, int number);
}
