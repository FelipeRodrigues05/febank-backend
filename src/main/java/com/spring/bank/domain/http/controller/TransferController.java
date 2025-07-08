package com.spring.bank.domain.http.controller;

import com.spring.bank.domain.dto.transfer.CreateTransferDTO;
import com.spring.bank.domain.dto.transfer.TransferResponseDTO;
import com.spring.bank.domain.model.Transfer;
import com.spring.bank.domain.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(name = "transfers", path = "/transfer")
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;

    @PostMapping("/create")
    public ResponseEntity<TransferResponseDTO> createTransfer(@Valid @RequestBody CreateTransferDTO body) {
        Transfer transfer = this.transferService.create(body);

        return ResponseEntity.status(HttpStatus.CREATED).body(new TransferResponseDTO(transfer));
    }
}
