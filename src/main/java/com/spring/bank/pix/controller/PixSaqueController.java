package com.spring.bank.pix.controller;

import com.spring.bank.pix.dto.CreatePixSaqueDTO;
import com.spring.bank.pix.dto.PixSaqueResponseDTO;
import com.spring.bank.pix.service.PixSaqueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pix")
@RequiredArgsConstructor
public class PixSaqueController {

    private final PixSaqueService pixSaqueService;

    @PostMapping("/saque")
    public ResponseEntity<PixSaqueResponseDTO> execute(@Valid @RequestBody CreatePixSaqueDTO body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PixSaqueResponseDTO(pixSaqueService.execute(body)));
    }
}
