package com.spring.bank.domain.dto.user;

public record RegisterDTO(
        String name,
        String document,
        String email,
        String password
) {
}
