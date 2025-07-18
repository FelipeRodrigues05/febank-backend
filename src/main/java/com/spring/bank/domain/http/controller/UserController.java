package com.spring.bank.domain.http.controller;

import com.spring.bank.domain.dto.user.UpdateUserDTO;
import com.spring.bank.domain.dto.user.UserResponseDTO;
import com.spring.bank.domain.model.User;
import com.spring.bank.domain.service.UserService;
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
    public ResponseEntity<UserResponseDTO> updateUser(@Valid @RequestBody UpdateUserDTO body, @PathVariable("id") Long id) {
        User user = this.userService.update(id, body);

        return ResponseEntity.status(HttpStatus.OK).body(new UserResponseDTO(user));
    }
}
