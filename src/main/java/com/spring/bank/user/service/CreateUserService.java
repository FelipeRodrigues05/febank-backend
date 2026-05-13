package com.spring.bank.user.service;

import com.spring.bank.common.exception.EmailAlreadyExistsException;
import com.spring.bank.user.dto.CreateUserDTO;
import com.spring.bank.user.enums.UserType;
import com.spring.bank.user.model.User;
import com.spring.bank.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateUserService {

    private static final Logger log = LoggerFactory.getLogger(CreateUserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
        user.setPassword(passwordEncoder.encode(data.password()));
        user.setType(digits.length() == 14 ? UserType.COMPANY : UserType.INDIVIDUAL);

        User saved = userRepository.save(user);
        log.info("User created: id={} email={} type={}", saved.getId(), saved.getEmail(), saved.getType());
        return saved;
    }
}
