package com.spring.bank.domain.dto.user;

import com.spring.bank.domain.model.Account;
import com.spring.bank.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private UUID id;
    private String name;
    private String document;
    private String email;

    private List<Account> accounts;
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public UserResponseDTO(User user) {
        this(
                user.getId(),
                user.getName(),
                user.getDocument(),
                user.getEmail(),
                user.getAccounts(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
