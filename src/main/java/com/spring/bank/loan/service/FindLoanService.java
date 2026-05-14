package com.spring.bank.loan.service;

import com.spring.bank.loan.dto.LoanInstallmentResponseDTO;
import com.spring.bank.loan.dto.LoanResponseDTO;
import com.spring.bank.loan.model.Loan;
import com.spring.bank.loan.repository.LoanInstallmentRepository;
import com.spring.bank.loan.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindLoanService {

    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;

    public Loan getById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found: " + id));
    }

    public List<LoanResponseDTO> listByAccount(Long accountId) {
        return loanRepository.findByAccountId(accountId).stream()
                .map(LoanResponseDTO::new)
                .toList();
    }

    public List<LoanInstallmentResponseDTO> getInstallments(Long loanId) {
        return loanInstallmentRepository.findByLoanIdOrderByInstallmentNumber(loanId).stream()
                .map(LoanInstallmentResponseDTO::new)
                .toList();
    }
}
