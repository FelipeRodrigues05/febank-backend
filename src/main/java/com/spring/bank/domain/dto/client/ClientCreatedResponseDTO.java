package com.spring.bank.domain.dto.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spring.bank.domain.model.Client;

import java.time.LocalDateTime;

public record ClientCreatedResponseDTO(
        String id,
        String clientId,
        String name,
        boolean active,
        @JsonProperty("client_secret") String clientSecret,
        LocalDateTime createdAt
) {
    public ClientCreatedResponseDTO(Client client, String rawSecret) {
        this(client.getId(), client.getClientId(), client.getName(), client.isActive(), rawSecret, client.getCreatedAt());
    }
}
