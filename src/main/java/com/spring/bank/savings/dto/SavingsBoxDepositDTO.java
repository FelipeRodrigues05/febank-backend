package com.spring.bank.savings.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SavingsBoxDepositDTO(@NotNull @DecimalMin("0.01") BigDecimal amount) {}
