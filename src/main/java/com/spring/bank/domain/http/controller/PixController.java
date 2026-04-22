package com.spring.bank.domain.http.controller;

import com.spring.bank.domain.dto.pix.CreatePixKeyDTO;
import com.spring.bank.domain.dto.pix.PixKeyResponseDTO;
import com.spring.bank.domain.dto.pix.PixTransferDTO;
import com.spring.bank.domain.dto.transfer.TransferResponseDTO;
import com.spring.bank.domain.model.Transfer;
import com.spring.bank.domain.service.PixService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pix")
@RequiredArgsConstructor
public class PixController {

    private final PixService pixService;

    @PostMapping("/keys")
    public ResponseEntity<PixKeyResponseDTO> registerKey(@Valid @RequestBody CreatePixKeyDTO body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new PixKeyResponseDTO(pixService.registerKey(body)));
    }

    @DeleteMapping("/keys/{id}")
    public ResponseEntity<Void> deleteKey(@PathVariable String id) {
        pixService.deleteKey(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/keys/account/{accountId}")
    public ResponseEntity<List<PixKeyResponseDTO>> listByAccount(@PathVariable Long accountId) {
        List<PixKeyResponseDTO> keys = pixService.listByAccount(accountId)
                .stream()
                .map(PixKeyResponseDTO::new)
                .toList();
        return ResponseEntity.ok(keys);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponseDTO> transfer(@Valid @RequestBody PixTransferDTO body) {
        Transfer transfer = pixService.transfer(body);
        return ResponseEntity.ok(new TransferResponseDTO(transfer));
    }
}
