package com.spring.bank.domain.http.controller;

import com.spring.bank.domain.dto.pix.*;
import com.spring.bank.domain.dto.transfer.TransferResponseDTO;
import com.spring.bank.domain.model.Transfer;
import com.spring.bank.domain.service.PixContactService;
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
    private final PixContactService pixContactService;

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

    @PostMapping("/contacts")
    public ResponseEntity<PixContactResponseDTO> addContact(@Valid @RequestBody CreatePixContactDTO body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PixContactResponseDTO(pixContactService.add(body)));
    }

    @GetMapping("/contacts/account/{accountId}")
    public ResponseEntity<List<PixContactResponseDTO>> listContacts(@PathVariable Long accountId) {
        return ResponseEntity.ok(pixContactService.listByAccount(accountId));
    }

    @PatchMapping("/contacts/{id}")
    public ResponseEntity<PixContactResponseDTO> updateContact(
            @PathVariable Long id,
            @RequestParam Long accountId,
            @Valid @RequestBody UpdatePixContactDTO body) {
        return ResponseEntity.ok(new PixContactResponseDTO(pixContactService.updateAlias(id, accountId, body)));
    }

    @DeleteMapping("/contacts/{id}")
    public ResponseEntity<Void> removeContact(@PathVariable Long id, @RequestParam Long accountId) {
        pixContactService.remove(id, accountId);
        return ResponseEntity.noContent().build();
    }
}
