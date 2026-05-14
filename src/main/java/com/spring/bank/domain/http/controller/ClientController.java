package com.spring.bank.domain.http.controller;

import com.spring.bank.domain.dto.client.ClientCreatedResponseDTO;
import com.spring.bank.domain.dto.client.CreateClientDTO;
import com.spring.bank.domain.service.ClientService;
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

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientCreatedResponseDTO> create(@Valid @RequestBody CreateClientDTO body) {
        ClientCreatedResponseDTO response = clientService.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
