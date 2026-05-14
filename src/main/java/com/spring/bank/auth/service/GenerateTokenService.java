package com.spring.bank.auth.service;

import com.spring.bank.auth.dto.TokenResponseDTO;
import com.spring.bank.auth.model.Client;
import com.spring.bank.auth.repository.ClientRepository;
import com.spring.bank.auth.security.JwtTokenProvider;
import com.spring.bank.common.exception.ClientNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenerateTokenService {

    private static final Logger log = LoggerFactory.getLogger(GenerateTokenService.class);

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenResponseDTO generate(String clientId, String clientSecret) {
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
}
