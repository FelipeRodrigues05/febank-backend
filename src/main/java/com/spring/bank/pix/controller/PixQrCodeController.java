package com.spring.bank.pix.controller;

import com.spring.bank.pix.dto.CreatePixQrCodeDTO;
import com.spring.bank.pix.dto.DictQueryResponseDTO;
import com.spring.bank.pix.dto.PixQrCodeResponseDTO;
import com.spring.bank.pix.service.DictService;
import com.spring.bank.pix.service.GeneratePixQrCodeService;
import com.spring.bank.pix.service.PayPixQrCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/pix")
@RequiredArgsConstructor
public class PixQrCodeController {

    private final GeneratePixQrCodeService generatePixQrCodeService;
    private final PayPixQrCodeService payPixQrCodeService;
    private final DictService dictService;

    @PostMapping("/qrcode")
    public ResponseEntity<PixQrCodeResponseDTO> generate(@Valid @RequestBody CreatePixQrCodeDTO body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PixQrCodeResponseDTO(generatePixQrCodeService.generate(body)));
    }

    @DeleteMapping("/qrcode/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable String id, @RequestParam Long accountId) {
        generatePixQrCodeService.deactivate(id, accountId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/qrcode/{id}/pay")
    public ResponseEntity<Map<String, Object>> pay(
            @PathVariable String id,
            @RequestParam Long payerAccountId,
            @RequestParam(required = false) BigDecimal amount) {
        BigDecimal paid = payPixQrCodeService.pay(payerAccountId, id, amount);
        return ResponseEntity.ok(Map.of("paid", paid));
    }

    @GetMapping("/dict/{key}")
    public ResponseEntity<DictQueryResponseDTO> queryDict(@PathVariable String key) {
        return ResponseEntity.ok(dictService.query(key));
    }
}
