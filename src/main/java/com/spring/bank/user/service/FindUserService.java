package com.spring.bank.user.service;

import com.spring.bank.common.exception.UserNotFoundException;
import com.spring.bank.user.model.User;
import com.spring.bank.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindUserService {

    private final UserRepository userRepository;

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(
                String.format("User with ID %s does not exist", id)
        ));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(
                String.format("User with EMAIL %s does not exist", email)
        ));
    }

    public User getByDocument(String document) {
        return userRepository.findByDocument(document).orElseThrow(() -> new UserNotFoundException(
                String.format("User with DOCUMENT %s does not exist", document)
        ));
    }
}
