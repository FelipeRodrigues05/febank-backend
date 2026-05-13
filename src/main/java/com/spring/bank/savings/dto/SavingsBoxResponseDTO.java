package com.spring.bank.savings.dto;

import com.spring.bank.savings.model.SavingsBox;
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
public class SavingsBoxResponseDTO {

    private Long id;
    private String name;
    private String imageUrl;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SavingsBoxResponseDTO(SavingsBox box) {
        this.id        = box.getId();
        this.name      = box.getName();
        this.imageUrl  = box.getImageUrl();
        this.balance   = box.getBalance();
        this.createdAt = box.getCreatedAt();
        this.updatedAt = box.getUpdatedAt();
    }
}
