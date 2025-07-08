package com.spring.bank.domain.dto.user;

import com.spring.bank.common.validation.cpfcnpj.CpfCnpj;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record RegisterDTO(
        @NotNull
        String name,

        @CpfCnpj
        @NotNull
        String document,

        @NotNull
        @Email
        String email,

        @NotNull
        String password
) {
}
