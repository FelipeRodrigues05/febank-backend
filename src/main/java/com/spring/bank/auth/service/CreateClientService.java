package com.spring.bank.auth.service;

import com.spring.bank.auth.dto.ClientCreatedResponseDTO;
import com.spring.bank.auth.dto.CreateClientDTO;
import com.spring.bank.auth.model.Client;
import com.spring.bank.auth.repository.ClientRepository;
import com.spring.bank.common.exception.ClientAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class CreateClientService {

    private static final Logger log = LoggerFactory.getLogger(CreateClientService.class);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientCreatedResponseDTO create(CreateClientDTO data) {
        return create(data, null);
    }

    public ClientCreatedResponseDTO create(CreateClientDTO data, String fixedSecret) {
        if (clientRepository.existsByClientId(data.clientId())) {
            throw new ClientAlreadyExistsException("Client with this clientId already exists");
        }

        String rawSecret = (fixedSecret != null && !fixedSecret.isBlank()) ? fixedSecret : generateSecret();

        Client client = new Client();
        client.setClientId(data.clientId());
        client.setClientSecret(passwordEncoder.encode(rawSecret));
        client.setName(data.name());

        Client saved = clientRepository.save(client);
        log.info("Client created: clientId={}", saved.getClientId());

        return new ClientCreatedResponseDTO(saved, rawSecret);
    }

    public boolean existsByClientId(String clientId) {
        return clientRepository.existsByClientId(clientId);
    }

    private static String generateSecret() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
