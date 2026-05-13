package com.spring.bank.boleto.controller;

import com.spring.bank.boleto.dto.BoletoResponseDTO;
import com.spring.bank.boleto.dto.EmitBoletoDTO;
import com.spring.bank.boleto.dto.PayBoletoDTO;
import com.spring.bank.boleto.service.EmitBoletoService;
import com.spring.bank.boleto.service.FindBoletoService;
import com.spring.bank.boleto.service.PayBoletoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boleto")
@RequiredArgsConstructor
public class BoletoController {

    private final EmitBoletoService emitBoletoService;
    private final PayBoletoService payBoletoService;
    private final FindBoletoService findBoletoService;

    @PostMapping("/emit")
    public ResponseEntity<BoletoResponseDTO> emit(@Valid @RequestBody EmitBoletoDTO body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new BoletoResponseDTO(emitBoletoService.emit(body)));
    }

    @PostMapping("/pay")
    public ResponseEntity<BoletoResponseDTO> pay(@Valid @RequestBody PayBoletoDTO body) {
        return ResponseEntity.ok(new BoletoResponseDTO(payBoletoService.pay(body)));
    }

    @GetMapping("/{code}")
    public ResponseEntity<BoletoResponseDTO> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(new BoletoResponseDTO(findBoletoService.getByCode(code)));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<BoletoResponseDTO>> listByAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(
                findBoletoService.listByIssuer(accountId).stream().map(BoletoResponseDTO::new).toList()
        );
    }
}
