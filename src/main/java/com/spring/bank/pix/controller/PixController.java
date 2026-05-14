package com.spring.bank.pix.controller;

import com.spring.bank.pix.dto.*;
import com.spring.bank.pix.service.*;
import com.spring.bank.transfer.dto.TransferResponseDTO;
import com.spring.bank.transfer.model.Transfer;
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

    private final RegisterPixKeyService registerPixKeyService;
    private final DeletePixKeyService deletePixKeyService;
    private final FindPixKeyService findPixKeyService;
    private final PixTransferService pixTransferService;
    private final AddPixContactService addPixContactService;
    private final FindPixContactService findPixContactService;
    private final UpdatePixContactService updatePixContactService;

    @PostMapping("/keys")
    public ResponseEntity<PixKeyResponseDTO> registerKey(@Valid @RequestBody CreatePixKeyDTO body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new PixKeyResponseDTO(registerPixKeyService.register(body)));
    }

    @DeleteMapping("/keys/{id}")
    public ResponseEntity<Void> deleteKey(@PathVariable String id) {
        deletePixKeyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/keys/account/{accountId}")
    public ResponseEntity<List<PixKeyResponseDTO>> listByAccount(@PathVariable Long accountId) {
        List<PixKeyResponseDTO> keys = findPixKeyService.listByAccount(accountId)
                .stream()
                .map(PixKeyResponseDTO::new)
                .toList();
        return ResponseEntity.ok(keys);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponseDTO> transfer(@Valid @RequestBody PixTransferDTO body) {
        Transfer transfer = pixTransferService.transfer(body);
        return ResponseEntity.ok(new TransferResponseDTO(transfer));
    }

    @PostMapping("/contacts")
    public ResponseEntity<PixContactResponseDTO> addContact(@Valid @RequestBody CreatePixContactDTO body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PixContactResponseDTO(addPixContactService.add(body)));
    }

    @GetMapping("/contacts/account/{accountId}")
    public ResponseEntity<List<PixContactResponseDTO>> listContacts(@PathVariable Long accountId) {
        return ResponseEntity.ok(findPixContactService.listByAccount(accountId));
    }

    @PatchMapping("/contacts/{id}")
    public ResponseEntity<PixContactResponseDTO> updateContact(
            @PathVariable Long id,
            @RequestParam Long accountId,
            @Valid @RequestBody UpdatePixContactDTO body) {
        return ResponseEntity.ok(new PixContactResponseDTO(updatePixContactService.updateAlias(id, accountId, body)));
    }

    @DeleteMapping("/contacts/{id}")
    public ResponseEntity<Void> removeContact(@PathVariable Long id, @RequestParam Long accountId) {
        updatePixContactService.remove(id, accountId);
        return ResponseEntity.noContent().build();
    }
}
