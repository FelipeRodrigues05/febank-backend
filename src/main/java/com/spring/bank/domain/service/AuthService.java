package com.spring.bank.domain.service;

import com.spring.bank.common.exception.InvalidPasswordException;
import com.spring.bank.common.exception.UserAlreadyHasAccountException;
import com.spring.bank.common.exception.UserNotFoundException;
import com.spring.bank.domain.dto.user.LoginDTO;
import com.spring.bank.domain.dto.user.RegisterDTO;
import com.spring.bank.domain.model.User;
import com.spring.bank.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final UserService userService;

    public void register(RegisterDTO data) throws UserAlreadyHasAccountException {
        if (this.userRepository.existsByDocument(data.document())) {
            throw new UserAlreadyHasAccountException(
                    String.format("DOCUMENT %s already exists", data.document())
            );
        }

        userService.create(data);
    }

    public void login(LoginDTO data) throws UserNotFoundException, InvalidPasswordException {
        User user = this.userRepository.findByDocument(data.document()).orElseThrow(() -> new UserNotFoundException(
                String.format("User with DOCUMENT %s not found", data.document())
        ));

        if (!passwordEncoder.matches(data.password(), user.getPassword())) {
            throw new InvalidPasswordException("Invalid password");
        }
    }
}
