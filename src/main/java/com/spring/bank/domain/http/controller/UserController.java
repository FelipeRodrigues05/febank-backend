package com.spring.bank.domain.http.controller;

import com.spring.bank.domain.dto.user.UpdateUserDTO;
import com.spring.bank.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(name = "user", path = "/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateUser(@Validated @RequestBody UpdateUserDTO body, @RequestParam UUID id) {
        this.userService.update(id, body);

        return ResponseEntity.status(HttpStatus.OK).body("User updated successfully");
    }
}
