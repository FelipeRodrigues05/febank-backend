package com.spring.bank.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserDTO(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String document,
        @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") String password
) {}
