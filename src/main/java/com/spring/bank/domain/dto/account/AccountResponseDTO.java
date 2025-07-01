package com.spring.bank.domain.dto.account;

import com.spring.bank.domain.enums.account.AccountStatusEnum;
import com.spring.bank.domain.enums.account.AccountTypeEnum;
import com.spring.bank.domain.model.Account;
import com.spring.bank.domain.model.Transaction;
import com.spring.bank.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDTO {
    private UUID id;
    private User user;
    private String number;
    private AccountTypeEnum type;
    private BigDecimal balance;
    private AccountStatusEnum status;
    private List<Transaction> transactions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AccountResponseDTO(Account account) {
        this(
                account.getId(),
                account.getUser(),
                account.getNumber(),
                account.getType(),
                account.getBalance(),
                account.getStatus(),
                account.getTransactions(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }
}
