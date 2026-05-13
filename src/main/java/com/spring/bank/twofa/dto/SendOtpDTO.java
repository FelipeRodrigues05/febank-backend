package com.spring.bank.twofa.dto;

import jakarta.validation.constraints.NotNull;

public record SendOtpDTO(
        @NotNull Long userId
) {}
