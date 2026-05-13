package com.spring.bank.transaction.service;

import com.spring.bank.transaction.enums.TransactionCategoryEnum;
import org.springframework.stereotype.Service;

@Service
public class TransactionCategorizationService {

    public TransactionCategoryEnum categorize(String description) {
        if (description == null || description.isBlank()) {
            return TransactionCategoryEnum.OTHER;
        }

        String upperDescription = description.toUpperCase();

        if (upperDescription.contains("PIX") || upperDescription.contains("TRANSFER")) {
            return TransactionCategoryEnum.TRANSFER;
        }
        if (upperDescription.contains("INVESTMENT") || upperDescription.contains("CDB")
                || upperDescription.contains("LCI") || upperDescription.contains("LCA")
                || upperDescription.contains("TESOURO")) {
            return TransactionCategoryEnum.INVESTMENT;
        }
        if (upperDescription.contains("LOAN") || upperDescription.contains("INSTALLMENT")) {
            return TransactionCategoryEnum.LOAN;
        }
        if (upperDescription.contains("BOLETO")) {
            return TransactionCategoryEnum.BOLETO;
        }
        if (upperDescription.contains("SALARY") || upperDescription.contains("PAYROLL")) {
            return TransactionCategoryEnum.SALARY;
        }
        if (upperDescription.contains("CASHBACK")) {
            return TransactionCategoryEnum.CASHBACK;
        }
        if (upperDescription.contains("DEPOSIT") || upperDescription.contains("SAVINGS")) {
            return TransactionCategoryEnum.BILLS;
        }

        return TransactionCategoryEnum.OTHER;
    }
}
