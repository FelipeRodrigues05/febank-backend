package com.spring.bank.loan.service;

import com.spring.bank.account.enums.AccountStatusEnum;
import com.spring.bank.account.enums.AccountTypeEnum;
import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.AccountFundsService;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.loan.dto.ApplyLoanDTO;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplyLoanService {

    private static final int BASE_CREDIT_SCORE = 500;
    private static final BigDecimal HIGH_BALANCE_THRESHOLD = new BigDecimal("5000");
    private static final BigDecimal MEDIUM_BALANCE_THRESHOLD = new BigDecimal("1000");
    private static final int HIGH_BALANCE_BONUS = 150;
    private static final int MEDIUM_BALANCE_BONUS = 100;
    private static final int ACCOUNT_AGE_BONUS = 100;
    private static final int ACCOUNT_AGE_MONTHS = 6;
    private static final int MAX_CREDIT_SCORE = 1000;
    private static final int MIN_CREDIT_SCORE_FOR_APPROVAL = 400;

    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;
    private final FindAccountService findAccountService;
    private final SimulateLoanService simulateLoanService;
    private final AccountFundsService accountFundsService;
    private final CreateTransactionService createTransactionService;

    private int simulateCreditScore(Account account) {
        int score = BASE_CREDIT_SCORE;

        if (account.getBalance().compareTo(HIGH_BALANCE_THRESHOLD) > 0) {
            score += HIGH_BALANCE_BONUS;
        }
        if (account.getBalance().compareTo(MEDIUM_BALANCE_THRESHOLD) > 0) {
            score += MEDIUM_BALANCE_BONUS;
        }
        if (account.getCreatedAt() != null && account.getCreatedAt().isBefore(LocalDateTime.now().minusMonths(ACCOUNT_AGE_MONTHS))) {
            score += ACCOUNT_AGE_BONUS;
        }

        return Math.min(MAX_CREDIT_SCORE, score);
    }

    public Loan apply(ApplyLoanDTO dto) {
        Account account = findAccountService.getById(dto.accountId());

        if (account.getStatus() != AccountStatusEnum.ACTIVE) {
            throw new IllegalArgumentException("Account is not active");
        }
        if (account.getType() != AccountTypeEnum.CHECKING) {
            throw new IllegalArgumentException("Loans are only available for checking accounts");
        }

        int creditScore = simulateCreditScore(account);

        if (creditScore < MIN_CREDIT_SCORE_FOR_APPROVAL) {
            Loan rejectedLoan = new Loan();
            rejectedLoan.setAccount(account);
            rejectedLoan.setType(dto.type());
            rejectedLoan.setRequestedAmount(dto.requestedAmount());
            rejectedLoan.setTotalInstallments(dto.totalInstallments());
            rejectedLoan.setStatus(LoanStatus.REJECTED);
            rejectedLoan.setCreditScoreAtApplication(creditScore);
            return loanRepository.save(rejectedLoan);
        }

        BigDecimal rate = simulateLoanService.getRate(creditScore);
        BigDecimal installmentAmount = simulateLoanService.calculateInstallment(
                dto.requestedAmount(), rate, dto.totalInstallments()
        );

        Loan loan = new Loan();
        loan.setAccount(account);
        loan.setType(dto.type());
        loan.setRequestedAmount(dto.requestedAmount());
        loan.setApprovedAmount(dto.requestedAmount());
        loan.setInterestRate(rate);
        loan.setTotalInstallments(dto.totalInstallments());
        loan.setPaidInstallments(0);
        loan.setInstallmentAmount(installmentAmount);
        loan.setStatus(LoanStatus.APPROVED);
        loan.setCreditScoreAtApplication(creditScore);

        Loan savedLoan = loanRepository.save(loan);

        List<LoanInstallment> installments = new ArrayList<>();
        for (int installmentIndex = 1; installmentIndex <= dto.totalInstallments(); installmentIndex++) {
            LoanInstallment installment = new LoanInstallment();
            installment.setLoan(savedLoan);
            installment.setInstallmentNumber(installmentIndex);
            installment.setAmount(installmentAmount);
            installment.setDueDate(LocalDate.now().plusMonths(installmentIndex));
            installment.setPaid(false);
            installments.add(installment);
        }
        loanInstallmentRepository.saveAll(installments);

        accountFundsService.addFunds(account.getId(), dto.requestedAmount());

        createTransactionService.create(new CreateTransactionDTO(
                account,
                TransactionTypeEnum.CREDIT,
                dto.requestedAmount(),
                "LOAN DISBURSEMENT #" + savedLoan.getId()
        ));

        savedLoan.setStatus(LoanStatus.ACTIVE);
        return loanRepository.save(savedLoan);
    }
}
