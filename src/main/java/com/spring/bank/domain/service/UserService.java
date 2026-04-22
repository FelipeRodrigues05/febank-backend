package com.spring.bank.domain.service;

import com.spring.bank.common.exception.EmailAlreadyExistsException;
import com.spring.bank.common.exception.UserNotFoundException;
import com.spring.bank.domain.dto.user.CreateUserDTO;
import com.spring.bank.domain.dto.user.UpdateUserDTO;
import com.spring.bank.domain.enums.user.UserType;
import com.spring.bank.domain.model.User;
import com.spring.bank.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Transactional
    public User create(CreateUserDTO data) {
        if (userRepository.existsByEmail(data.email())) {
            throw new EmailAlreadyExistsException("User with this EMAIL already exists");
        }

        String digits = data.document().replaceAll("\\D", "");

        User user = new User();
        user.setName(data.name());
        user.setEmail(data.email().trim().toLowerCase());
        user.setDocument(data.document());
        user.setType(digits.length() == 14 ? UserType.COMPANY : UserType.INDIVIDUAL);

        User saved = this.userRepository.save(user);
        log.info("User created: id={} email={} type={}", saved.getId(), saved.getEmail(), saved.getType());
        return saved;
    }

    @Transactional
    public User update(Long id, UpdateUserDTO data) {
        User user = this.getById(id);

        if (data.name() != null && !data.name().isBlank()) user.setName(data.name());
        if (data.email() != null && !user.getEmail().equals(data.email())) {
            if (userRepository.existsByEmail(data.email()))
                throw new EmailAlreadyExistsException("User with this EMAIL already exists");
            user.setEmail(data.email().trim().toLowerCase());
        }

        User saved = this.userRepository.save(user);
        log.info("User updated: id={}", id);
        return saved;
    }

    public User getById(Long id) {
        return this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(
                String.format("User with ID %s does not exist", id)
        ));
    }

    public User getByEmail(String email) {
        return this.userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(
                String.format("User with EMAIL %s does not exist", email)
        ));
    }

    public User getByDocument(String document) {
        return this.userRepository.findByDocument(document).orElseThrow(() -> new UserNotFoundException(
                String.format("User with DOCUMENT %s does not exist", document)
        ));
    }
}
