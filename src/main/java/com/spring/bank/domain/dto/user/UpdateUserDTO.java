package com.spring.bank.domain.dto.user;

import java.util.UUID;

public record UpdateUserDTO(
        String name,
        String email,
        String password
) {
}
