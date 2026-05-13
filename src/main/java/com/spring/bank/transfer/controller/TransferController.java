package com.spring.bank.transfer.controller;

import com.spring.bank.transfer.dto.CreateTransferDTO;
import com.spring.bank.transfer.dto.TransferResponseDTO;
import com.spring.bank.transfer.model.Transfer;
import com.spring.bank.transfer.service.CreateTransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final CreateTransferService createTransferService;

    @PostMapping("/create")
    public ResponseEntity<TransferResponseDTO> createTransfer(@Valid @RequestBody CreateTransferDTO body) {
        Transfer transfer = createTransferService.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TransferResponseDTO(transfer));
    }
}
