package com.spring.bank.transfer.dto;

import com.spring.bank.account.dto.AccountResponseDTO;
import com.spring.bank.transfer.enums.TransferStatusEnum;
import com.spring.bank.transfer.model.Transfer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferResponseDTO {

    private Long id;
    private AccountResponseDTO fromAccount;
    private AccountResponseDTO toAccount;
    private BigDecimal amount;
    private String description;
    private TransferStatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TransferResponseDTO(Transfer transfer) {
        this.id          = transfer.getId();
        this.fromAccount = new AccountResponseDTO(transfer.getFromAccount());
        this.toAccount   = new AccountResponseDTO(transfer.getToAccount());
        this.amount      = transfer.getAmount();
        this.description = transfer.getDescription();
        this.status      = transfer.getStatus();
        this.createdAt   = transfer.getCreatedAt();
        this.updatedAt   = transfer.getUpdatedAt();
    }
}
