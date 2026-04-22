package com.spring.bank.domain.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserDTO(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String document
) {}
