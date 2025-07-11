package com.spring.bank.domain.service;

import com.spring.bank.common.exception.EmailAlreadyExistsException;
import com.spring.bank.common.exception.UserNotFoundException;
import com.spring.bank.domain.dto.user.RegisterDTO;
import com.spring.bank.domain.dto.user.UpdateUserDTO;
import com.spring.bank.domain.model.User;
import com.spring.bank.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User create(RegisterDTO data) {
        User user = new User();
        user.setName(data.name());
        user.setEmail(data.email());
        user.setDocument(data.document());
        user.setPassword(passwordEncoder.encode(data.password()));
        user.setCreatedAt(LocalDateTime.now());

        return this.userRepository.save(user);
    }

    @Transactional
    public User update(Long id, UpdateUserDTO data) throws UserNotFoundException, EmailAlreadyExistsException {
        User user = this.getById(id);

        if (data.name() != null) user.setName(data.name());
        if (data.password() != null && !data.password().isBlank())
            user.setPassword(passwordEncoder.encode(data.password()));
        if (data.email() != null && !user.getEmail().equals(data.email())) {
            if (userRepository.existsByEmail(data.email()))
                throw new EmailAlreadyExistsException("User with this EMAIL already exists");

            user.setEmail(data.email());
        }

        user.setUpdatedAt(LocalDateTime.now());

        return this.userRepository.save(user);
    }

    public User getById(Long id) throws UserNotFoundException {
        return this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(
                String.format("User with ID %s does not exist", id)
        ));
    }

    public User getByEmail(String email) throws UserNotFoundException {
        return this.userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(
                String.format("User with EMAIL %s does not exist", email)
        ));
    }

    public User getByDocument(String document) throws UserNotFoundException {
        return this.userRepository.findByDocument(document).orElseThrow(() -> new UserNotFoundException(
                String.format("User with DOCUMENT %s does not exist", document)
        ));
    }
}
