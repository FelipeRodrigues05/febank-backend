package com.spring.bank.domain.http.controller;

import com.spring.bank.domain.dto.client.TokenResponseDTO;
import com.spring.bank.domain.service.ClientService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class TokenController {

    private static final Logger log = LoggerFactory.getLogger(TokenController.class);

    private final ClientService clientService;

    @PostMapping("/token")
    public ResponseEntity<TokenResponseDTO> token(
            HttpServletRequest request,
            @RequestParam(value = "grant_type", required = false, defaultValue = "client_credentials") String grantType,
            @RequestParam(value = "client_id", required = false) String clientIdParam,
            @RequestParam(value = "client_secret", required = false) String clientSecretParam
    ) {
        if (!"client_credentials".equals(grantType)) {
            return ResponseEntity.badRequest().build();
        }

        String clientId = clientIdParam;
        String clientSecret = clientSecretParam;

        if (clientId == null || clientSecret == null) {
            String[] basic = extractBasicAuth(request);
            if (basic != null) {
                clientId = basic[0];
                clientSecret = basic[1];
            }
        }

        if (clientId == null || clientSecret == null) {
            log.warn("Token request missing credentials — no body params and no Basic Auth header");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.debug("Token request for clientId={}", clientId);
        TokenResponseDTO response = clientService.generateToken(clientId, clientSecret);
        return ResponseEntity.ok(response);
    }

    private String[] extractBasicAuth(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Basic ")) return null;
        try {
            String decoded = new String(Base64.getDecoder().decode(header.substring(6)));
            int colon = decoded.indexOf(':');
            if (colon <= 0) return null;
            return new String[]{ decoded.substring(0, colon), decoded.substring(colon + 1) };
        } catch (IllegalArgumentException e) {
            log.warn("Invalid Base64 in Basic Auth header");
            return null;
        }
    }
}
