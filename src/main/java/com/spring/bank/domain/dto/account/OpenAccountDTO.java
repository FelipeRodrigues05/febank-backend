package com.spring.bank.domain.dto.account;

import com.spring.bank.domain.enums.account.AccountTypeEnum;
import com.spring.bank.domain.model.User;

public record OpenAccountDTO(
        User user,
        AccountTypeEnum type
) {
}
