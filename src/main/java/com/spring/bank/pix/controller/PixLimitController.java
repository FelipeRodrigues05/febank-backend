package com.spring.bank.pix.controller;

import com.spring.bank.pix.dto.PixLimitResponseDTO;
import com.spring.bank.pix.dto.UpdatePixLimitDTO;
import com.spring.bank.pix.service.PixLimitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pix")
@RequiredArgsConstructor
public class PixLimitController {

    private final PixLimitService pixLimitService;

    @GetMapping("/limits/{accountId}")
    public ResponseEntity<PixLimitResponseDTO> getLimits(@PathVariable Long accountId) {
        return ResponseEntity.ok(new PixLimitResponseDTO(pixLimitService.getOrCreateDefault(accountId)));
    }

    @PutMapping("/limits/{accountId}")
    public ResponseEntity<PixLimitResponseDTO> updateLimits(
            @PathVariable Long accountId,
            @Valid @RequestBody UpdatePixLimitDTO body) {
        return ResponseEntity.ok(pixLimitService.update(accountId, body));
    }
}
