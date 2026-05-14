package com.spring.bank.pix.dto;

import com.spring.bank.pix.enums.PixDevolutionStatus;
import com.spring.bank.pix.model.PixDevolution;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PixDevolutionResponseDTO(
        Long id,
        Long originalTransferId,
        Long requesterAccountId,
        BigDecimal amount,
        String reason,
        PixDevolutionStatus status,
        LocalDateTime createdAt
) {
    public PixDevolutionResponseDTO(PixDevolution devolution) {
        this(
                devolution.getId(),
                devolution.getOriginalTransferId(),
                devolution.getRequesterAccountId(),
                devolution.getAmount(),
                devolution.getReason(),
                devolution.getStatus(),
                devolution.getCreatedAt()
        );
    }
}
