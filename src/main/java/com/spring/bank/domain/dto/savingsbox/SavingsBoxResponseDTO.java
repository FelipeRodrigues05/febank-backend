package com.spring.bank.domain.dto.savingsbox;

import com.spring.bank.domain.model.SavingsBox;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingsBoxResponseDTO {
    private Long id;
    private Long accountId;
    private String name;
    private String imageUrl;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SavingsBoxResponseDTO(SavingsBox box) {
        this.id = box.getId();
        this.accountId = box.getAccount().getId();
        this.name = box.getName();
        this.imageUrl = box.getImageUrl();
        this.balance = box.getBalance();
        this.createdAt = box.getCreatedAt();
        this.updatedAt = box.getUpdatedAt();
    }
}
