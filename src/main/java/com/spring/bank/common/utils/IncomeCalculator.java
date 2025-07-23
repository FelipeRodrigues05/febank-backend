package com.spring.bank.common.utils;

import org.springframework.stereotype.Component;

@Component
public class IncomeCalculator {
    private static final double ANNUAL_CDI = 0.1;
    private static final double BUSINESS_DAYS = 252;

    // Daily return based on annual CDI
    public double daily() {
        return Math.pow(1 + ANNUAL_CDI, 1.0 / BUSINESS_DAYS) - 1;
    }

    // Simulate earnings for an invested amount over a number of business days
    public double income(double initialAmount, int days) {
        double dailyYield = this.daily();
        double finalAmount = initialAmount * Math.pow(1 + dailyYield, days);
        return Math.floor(finalAmount - initialAmount);
    }
}
