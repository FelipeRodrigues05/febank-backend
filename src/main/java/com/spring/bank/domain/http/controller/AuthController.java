package com.spring.bank.domain.http.controller;

import com.spring.bank.domain.dto.user.LoginDTO;
import com.spring.bank.domain.dto.user.RegisterDTO;
import com.spring.bank.domain.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(name = "auth", path = "/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Validated @RequestBody RegisterDTO body) {
        this.authService.register(body);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Validated @RequestBody LoginDTO body) {
        this.authService.login(body);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
