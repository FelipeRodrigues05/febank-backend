package com.spring.bank.pix.controller;

import com.spring.bank.pix.dto.CreatePixDevolutionDTO;
import com.spring.bank.pix.dto.PixDevolutionResponseDTO;
import com.spring.bank.pix.service.PixDevolutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pix")
@RequiredArgsConstructor
public class PixDevolutionController {

    private final PixDevolutionService pixDevolutionService;

    @PostMapping("/devolution")
    public ResponseEntity<PixDevolutionResponseDTO> request(@Valid @RequestBody CreatePixDevolutionDTO body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PixDevolutionResponseDTO(pixDevolutionService.request(body)));
    }

    @PatchMapping("/devolution/{id}/approve")
    public ResponseEntity<PixDevolutionResponseDTO> approve(@PathVariable Long id) {
        return ResponseEntity.ok(new PixDevolutionResponseDTO(pixDevolutionService.approve(id)));
    }

    @PatchMapping("/devolution/{id}/reject")
    public ResponseEntity<PixDevolutionResponseDTO> reject(@PathVariable Long id) {
        return ResponseEntity.ok(new PixDevolutionResponseDTO(pixDevolutionService.reject(id)));
    }

    @GetMapping("/devolution/account/{accountId}")
    public ResponseEntity<List<PixDevolutionResponseDTO>> listByAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(pixDevolutionService.listByAccount(accountId));
    }
}
