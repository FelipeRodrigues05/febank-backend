package com.spring.bank.account.dto;

import com.spring.bank.account.enums.AccountTypeEnum;
import jakarta.validation.constraints.NotNull;

public record OpenAccountDTO(
        @NotNull Long userId,
        @NotNull AccountTypeEnum type
) {}
