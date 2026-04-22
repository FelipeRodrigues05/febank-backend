package com.spring.bank.domain.dto.cardbill;

import jakarta.validation.constraints.NotNull;

public record PayBillDTO(
        @NotNull Long accountId
) {
}
