package com.spring.bank.auth.service;

import com.spring.bank.auth.dto.LoginRequestDTO;
import com.spring.bank.auth.dto.LoginResponseDTO;
import com.spring.bank.auth.security.JwtTokenProvider;
import com.spring.bank.common.exception.InvalidCredentialsException;
import com.spring.bank.user.dto.UserResponseDTO;
import com.spring.bank.user.model.User;
import com.spring.bank.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponseDTO login(LoginRequestDTO data) {
        String normalizedEmail = data.email().trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail).orElse(null);
        if (user == null) {
            log.warn("Login failed: no user found for email={}", normalizedEmail);
            throw new InvalidCredentialsException("Invalid credentials");
        }

        if (user.getPassword() == null) {
            log.warn("Login failed: user id={} has no password set", user.getId());
            throw new InvalidCredentialsException("Invalid credentials");
        }

        if (!passwordEncoder.matches(data.password(), user.getPassword())) {
            log.warn("Login failed: wrong password for user id={}", user.getId());
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String token = jwtTokenProvider.generateUserToken(user.getId(), user.getEmail());
        log.info("User logged in: id={}", user.getId());

        return new LoginResponseDTO(token, "Bearer", jwtTokenProvider.getExpirationSeconds(), new UserResponseDTO(user));
    }
}
