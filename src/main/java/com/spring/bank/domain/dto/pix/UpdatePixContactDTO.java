package com.spring.bank.domain.dto.pix;

import jakarta.validation.constraints.NotBlank;

public record UpdatePixContactDTO(@NotBlank String alias) {}
