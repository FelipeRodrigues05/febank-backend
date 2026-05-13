package com.spring.bank.user.controller;

import com.spring.bank.user.dto.CreateUserDTO;
import com.spring.bank.user.dto.UpdateUserDTO;
import com.spring.bank.user.dto.UserResponseDTO;
import com.spring.bank.user.model.User;
import com.spring.bank.user.service.CreateUserService;
import com.spring.bank.user.service.FindUserService;
import com.spring.bank.user.service.UpdateUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserService createUserService;
    private final FindUserService findUserService;
    private final UpdateUserService updateUserService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody CreateUserDTO body) {
        User user = createUserService.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponseDTO(user));
    }

    @GetMapping
    public ResponseEntity<UserResponseDTO> getUser(@RequestParam Long userId) {
        User user = findUserService.getById(userId);
        return ResponseEntity.ok(new UserResponseDTO(user));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@Valid @RequestBody UpdateUserDTO body, @PathVariable Long id) {
        User user = updateUserService.update(id, body);
        return ResponseEntity.ok(new UserResponseDTO(user));
    }
}
