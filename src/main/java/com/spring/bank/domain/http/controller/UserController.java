package com.spring.bank.domain.http.controller;

import com.spring.bank.domain.dto.user.ChangePasswordDTO;
import com.spring.bank.domain.dto.user.ForgotPasswordDTO;
import com.spring.bank.domain.dto.user.UpdateUserDTO;
import com.spring.bank.domain.dto.user.UserResponseDTO;
import com.spring.bank.domain.model.User;
import com.spring.bank.domain.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(name = "user", path = "/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@Valid @RequestBody UpdateUserDTO body, @PathVariable Long id) {
        User user = this.userService.update(id, body);

        return ResponseEntity.status(HttpStatus.OK).body(new UserResponseDTO(user));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<HttpStatus> forgotPassword(@Valid @RequestBody ForgotPasswordDTO body) {
        try {
            userService.forgotPassword(body.email());

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/change-password/{id}")
    public ResponseEntity<HttpStatus> changePassword(@Valid @RequestBody ChangePasswordDTO body, @PathVariable Long id) {
        User user = this.userService.getById(id);

        this.userService.changePassword(user, body.password());

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
