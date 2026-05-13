package com.spring.bank.loan.service;

import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.common.exception.InsufficientFundsException;
import com.spring.bank.loan.dto.PayInstallmentDTO;
import com.spring.bank.loan.enums.LoanStatus;
import com.spring.bank.loan.model.Loan;
import com.spring.bank.loan.model.LoanInstallment;
import com.spring.bank.loan.repository.LoanInstallmentRepository;
import com.spring.bank.loan.repository.LoanRepository;
import com.spring.bank.transaction.dto.CreateTransactionDTO;
import com.spring.bank.transaction.enums.TransactionTypeEnum;
import com.spring.bank.transaction.service.CreateTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PayLoanInstallmentService {

    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;
    private final FindAccountService findAccountService;
    private final AccountFundsService accountFundsService;
    private final CreateTransactionService createTransactionService;

    public LoanInstallment pay(PayInstallmentDTO dto) {
        Loan loan = loanRepository.findById(dto.loanId())
                .orElseThrow(() -> new IllegalArgumentException("Loan not found: " + dto.loanId()));

        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new IllegalArgumentException("Loan is not active (status: " + loan.getStatus() + ")");
        }

        Account account = findAccountService.getById(dto.accountId());

        if (!account.getId().equals(loan.getAccount().getId())) {
            throw new IllegalArgumentException("Account does not belong to this loan");
        }

        LoanInstallment installment = loanInstallmentRepository
                .findByLoanIdAndInstallmentNumber(dto.loanId(), dto.installmentNumber())
                .orElseThrow(() -> new IllegalArgumentException("Installment not found: #" + dto.installmentNumber()));

        if (installment.isPaid()) {
            throw new IllegalArgumentException("Installment already paid");
        }

        if (account.getBalance().compareTo(installment.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds to pay installment");
        }

        accountFundsService.subtractFunds(dto.accountId(), installment.getAmount());

        createTransactionService.create(new CreateTransactionDTO(
                account,
                TransactionTypeEnum.DEBIT,
                installment.getAmount(),
                "LOAN INSTALLMENT #" + dto.installmentNumber() + " - Loan #" + dto.loanId()
        ));

        installment.setPaid(true);
        installment.setPaidAt(LocalDateTime.now());
        loanInstallmentRepository.save(installment);

        loan.setPaidInstallments(loan.getPaidInstallments() + 1);

        if (loan.getPaidInstallments() == loan.getTotalInstallments()) {
            loan.setStatus(LoanStatus.PAID_OFF);
        }

        loanRepository.save(loan);

        return installment;
    }
}
