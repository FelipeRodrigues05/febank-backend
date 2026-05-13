package com.spring.bank.pix.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePixContactDTO(@NotBlank String alias) {}
