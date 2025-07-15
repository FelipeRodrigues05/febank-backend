package com.spring.bank.domain.dto.account;

import com.spring.bank.domain.enums.account.AccountTypeEnum;
import jakarta.validation.constraints.NotNull;

public record OpenAccountDTO(
        @NotNull
        Long userId,

        @NotNull
        AccountTypeEnum type
) {
}
