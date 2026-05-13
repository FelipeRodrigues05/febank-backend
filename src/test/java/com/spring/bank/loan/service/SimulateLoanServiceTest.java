package com.spring.bank.loan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class SimulateLoanServiceTest {

    private SimulateLoanService simulateLoanService;

    @BeforeEach
    void setUp() {
        simulateLoanService = new SimulateLoanService();
    }

    @Test
    void getRate_shouldReturn1_5ForExcellentScore() {
        BigDecimal rate = simulateLoanService.getRate(850);

        assertThat(rate).isEqualByComparingTo(new BigDecimal("0.015"));
    }

    @Test
    void getRate_shouldReturn2_5ForGoodScore() {
        BigDecimal rate = simulateLoanService.getRate(700);

        assertThat(rate).isEqualByComparingTo(new BigDecimal("0.025"));
    }

    @Test
    void getRate_shouldReturn4_0ForRegularScore() {
        BigDecimal rate = simulateLoanService.getRate(500);

        assertThat(rate).isEqualByComparingTo(new BigDecimal("0.040"));
    }

    @Test
    void getRate_shouldReturn6_0ForBadScore() {
        BigDecimal rate = simulateLoanService.getRate(300);

        assertThat(rate).isEqualByComparingTo(new BigDecimal("0.060"));
    }

    @Test
    void calculateInstallment_shouldUseTabelaPrice() {
        BigDecimal principal = new BigDecimal("10000");
        BigDecimal monthlyRate = new BigDecimal("0.025");
        int installmentCount = 12;

        BigDecimal result = simulateLoanService.calculateInstallment(principal, monthlyRate, installmentCount);

        assertThat(result).isGreaterThan(new BigDecimal("900")).isLessThan(new BigDecimal("1000"));
    }
}
