package com.spring.bank.domain.service;

import com.spring.bank.common.config.security.JwtTokenProvider;
import com.spring.bank.common.exception.ClientAlreadyExistsException;
import com.spring.bank.common.exception.ClientNotFoundException;
import com.spring.bank.domain.dto.client.ClientCreatedResponseDTO;
import com.spring.bank.domain.dto.client.CreateClientDTO;
import com.spring.bank.domain.dto.client.TokenResponseDTO;
import com.spring.bank.domain.model.Client;
import com.spring.bank.domain.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class ClientService {

    private static final Logger log = LoggerFactory.getLogger(ClientService.class);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

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

    public TokenResponseDTO generateToken(String clientId, String clientSecret) {
        Client client = clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Invalid client credentials"));

        if (!client.isActive()) {
            throw new ClientNotFoundException("Client is inactive");
        }

        if (!passwordEncoder.matches(clientSecret, client.getClientSecret())) {
            throw new ClientNotFoundException("Invalid client credentials");
        }

        String token = jwtTokenProvider.generateToken(client.getClientId(), client.getName());
        log.info("Token issued for clientId={}", clientId);

        return new TokenResponseDTO(token, "Bearer", jwtTokenProvider.getExpirationSeconds());
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
