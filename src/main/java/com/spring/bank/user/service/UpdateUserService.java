package com.spring.bank.user.service;

import com.spring.bank.common.exception.EmailAlreadyExistsException;
import com.spring.bank.user.dto.UpdateUserDTO;
import com.spring.bank.user.model.User;
import com.spring.bank.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateUserService {

    private static final Logger log = LoggerFactory.getLogger(UpdateUserService.class);

    private final UserRepository userRepository;
    private final FindUserService findUserService;

    @Transactional
    public User update(Long id, UpdateUserDTO data) {
        User user = findUserService.getById(id);

        if (data.name() != null && !data.name().isBlank()) user.setName(data.name());
        if (data.email() != null && !user.getEmail().equals(data.email())) {
            if (userRepository.existsByEmail(data.email()))
                throw new EmailAlreadyExistsException("User with this EMAIL already exists");
            user.setEmail(data.email().trim().toLowerCase());
        }

        User saved = userRepository.save(user);
        log.info("User updated: id={}", id);
        return saved;
    }
}
