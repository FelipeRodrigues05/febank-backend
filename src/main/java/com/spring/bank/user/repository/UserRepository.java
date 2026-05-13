package com.spring.bank.user.repository;

import com.spring.bank.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByDocument(String document);
    boolean existsByEmail(String email);
    boolean existsByDocument(String document);
}
