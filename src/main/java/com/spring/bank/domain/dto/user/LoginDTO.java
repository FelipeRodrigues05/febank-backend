package com.spring.bank.domain.dto.user;

import com.spring.bank.common.validation.cpfcnpj.CpfCnpj;
import org.springframework.lang.NonNull;

public record LoginDTO(
        @CpfCnpj
        @NonNull
        String document,

        @NonNull
        String password
) {
}
