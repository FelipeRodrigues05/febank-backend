package com.spring.bank.domain.dto.client;

import com.spring.bank.domain.model.Client;

import java.time.LocalDateTime;

public record ClientResponseDTO(
        String id,
        String clientId,
        String name,
        boolean active,
        LocalDateTime createdAt
) {
    public ClientResponseDTO(Client client) {
        this(client.getId(), client.getClientId(), client.getName(), client.isActive(), client.getCreatedAt());
    }
}
