package com.spring.bank.compliance.dto;

import com.spring.bank.compliance.enums.KycStatus;
import com.spring.bank.compliance.model.KycRecord;

import java.time.LocalDateTime;

public record KycResponseDTO(
        Long userId,
        KycStatus status,
        LocalDateTime submittedAt,
        LocalDateTime reviewedAt
) {
    public KycResponseDTO(KycRecord record) {
        this(record.getUserId(), record.getStatus(), record.getSubmittedAt(), record.getReviewedAt());
    }
}
