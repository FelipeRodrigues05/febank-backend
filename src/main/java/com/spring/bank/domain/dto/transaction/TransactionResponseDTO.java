package com.spring.bank.domain.dto.transaction;

import com.spring.bank.domain.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO {
    private Long id;
    private BigDecimal amount;
    private String description;
    private String type;
    private LocalDateTime createdAt;

    public TransactionResponseDTO(Transaction transaction) {
        this.id             = transaction.getId();
        this.amount         = transaction.getAmount();
        this.description    = transaction.getDescription();
        this.type           = transaction.getType() != null ? transaction.getType().name() : null;
        this.createdAt      = transaction.getCreatedAt();
    }
}