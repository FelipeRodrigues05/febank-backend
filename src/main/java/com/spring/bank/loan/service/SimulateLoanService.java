package com.spring.bank.loan.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Service
public class SimulateLoanService {

    private static final BigDecimal RATE_SCORE_EXCELLENT = new BigDecimal("0.015");
    private static final BigDecimal RATE_SCORE_GOOD = new BigDecimal("0.025");
    private static final BigDecimal RATE_SCORE_REGULAR = new BigDecimal("0.040");
    private static final BigDecimal RATE_SCORE_BAD = new BigDecimal("0.060");

    private static final int SCORE_THRESHOLD_EXCELLENT = 800;
    private static final int SCORE_THRESHOLD_GOOD = 600;
    private static final int SCORE_THRESHOLD_REGULAR = 400;

    public BigDecimal getRate(int creditScore) {
        if (creditScore >= SCORE_THRESHOLD_EXCELLENT) {
            return RATE_SCORE_EXCELLENT;
        }
        if (creditScore >= SCORE_THRESHOLD_GOOD) {
            return RATE_SCORE_GOOD;
        }
        if (creditScore >= SCORE_THRESHOLD_REGULAR) {
            return RATE_SCORE_REGULAR;
        }
        return RATE_SCORE_BAD;
    }

    public BigDecimal calculateInstallment(BigDecimal principal, BigDecimal monthlyRate, int installments) {
        BigDecimal onePlusRate = monthlyRate.add(BigDecimal.ONE);
        BigDecimal compoundFactor = onePlusRate.pow(installments, MathContext.DECIMAL128);
        BigDecimal numerator = monthlyRate.multiply(compoundFactor);
        BigDecimal denominator = compoundFactor.subtract(BigDecimal.ONE);
        return principal.multiply(numerator.divide(denominator, 10, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
