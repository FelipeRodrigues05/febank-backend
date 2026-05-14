package com.spring.bank.transaction.service;

import com.spring.bank.transaction.enums.TransactionCategoryEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TransactionCategorizationServiceTest {

    @InjectMocks
    private TransactionCategorizationService transactionCategorizationService;

    @Test
    void categorize_shouldReturnTransferForPixKeyword() {
        TransactionCategoryEnum result = transactionCategorizationService.categorize("PIX payment");
        assertThat(result).isEqualTo(TransactionCategoryEnum.TRANSFER);
    }

    @Test
    void categorize_shouldReturnTransferForTransferKeyword() {
        TransactionCategoryEnum result = transactionCategorizationService.categorize("TRANSFER to account");
        assertThat(result).isEqualTo(TransactionCategoryEnum.TRANSFER);
    }

    @Test
    void categorize_shouldReturnInvestmentForCdbKeyword() {
        TransactionCategoryEnum result = transactionCategorizationService.categorize("CDB investment purchase");
        assertThat(result).isEqualTo(TransactionCategoryEnum.INVESTMENT);
    }

    @Test
    void categorize_shouldReturnLoanForLoanKeyword() {
        TransactionCategoryEnum result = transactionCategorizationService.categorize("LOAN installment payment");
        assertThat(result).isEqualTo(TransactionCategoryEnum.LOAN);
    }

    @Test
    void categorize_shouldReturnBoletoForBoletoKeyword() {
        TransactionCategoryEnum result = transactionCategorizationService.categorize("BOLETO payment");
        assertThat(result).isEqualTo(TransactionCategoryEnum.BOLETO);
    }

    @Test
    void categorize_shouldReturnSalaryForSalaryKeyword() {
        TransactionCategoryEnum result = transactionCategorizationService.categorize("SALARY deposit");
        assertThat(result).isEqualTo(TransactionCategoryEnum.SALARY);
    }

    @Test
    void categorize_shouldReturnCashbackForCashbackKeyword() {
        TransactionCategoryEnum result = transactionCategorizationService.categorize("CASHBACK reward");
        assertThat(result).isEqualTo(TransactionCategoryEnum.CASHBACK);
    }

    @Test
    void categorize_shouldReturnOtherForNullDescription() {
        TransactionCategoryEnum result = transactionCategorizationService.categorize(null);
        assertThat(result).isEqualTo(TransactionCategoryEnum.OTHER);
    }

    @Test
    void categorize_shouldReturnOtherForBlankDescription() {
        TransactionCategoryEnum result = transactionCategorizationService.categorize("   ");
        assertThat(result).isEqualTo(TransactionCategoryEnum.OTHER);
    }

    @Test
    void categorize_shouldReturnOtherForUnknownDescription() {
        TransactionCategoryEnum result = transactionCategorizationService.categorize("random grocery purchase");
        assertThat(result).isEqualTo(TransactionCategoryEnum.OTHER);
    }
}
