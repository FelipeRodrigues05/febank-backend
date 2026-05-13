package com.spring.bank.auth.controller;

import com.spring.bank.auth.dto.ClientCreatedResponseDTO;
import com.spring.bank.auth.dto.CreateClientDTO;
import com.spring.bank.auth.service.CreateClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final CreateClientService createClientService;

    @PostMapping
    public ResponseEntity<ClientCreatedResponseDTO> create(@Valid @RequestBody CreateClientDTO body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createClientService.create(body));
    }
}
