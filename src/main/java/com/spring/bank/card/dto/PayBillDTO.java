package com.spring.bank.card.dto;

import jakarta.validation.constraints.NotNull;

public record PayBillDTO(@NotNull Long accountId) {}
