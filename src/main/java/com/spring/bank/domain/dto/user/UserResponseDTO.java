package com.spring.bank.domain.dto.user;

import com.spring.bank.domain.dto.account.AccountResponseDTO;
import com.spring.bank.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String name;
    private String document;
    private String email;
    private List<AccountResponseDTO> accounts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.document = user.getDocument();
        this.name = user.getName();
        this.email = user.getEmail();

        if (user.getAccounts() != null) {
            this.accounts = user.getAccounts().stream()
                    .map(AccountResponseDTO::new)
                    .collect(Collectors.toList());
        } else {
            this.accounts = List.of();
        }

        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
}