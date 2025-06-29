package com.spring.bank.domain.repository;

import com.spring.bank.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByDocument(String document);
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByDocument(String document);
}
