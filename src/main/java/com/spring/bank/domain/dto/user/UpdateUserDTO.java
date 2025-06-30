package com.spring.bank.domain.dto.user;

public record UpdateUserDTO(
        String name,
        String email,
        String password
) {
}
