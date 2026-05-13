package com.spring.bank.auth.dto;

import com.spring.bank.auth.model.Client;

public record ClientCreatedResponseDTO(
        String id,
        String clientId,
        String clientSecret,
        String name
) {
    public ClientCreatedResponseDTO(Client client, String rawSecret) {
        this(client.getId(), client.getClientId(), rawSecret, client.getName());
    }
}
