package com.spring.bank.scheduling.controller;

import com.spring.bank.scheduling.dto.CreateScheduledPaymentDTO;
import com.spring.bank.scheduling.dto.ScheduledPaymentResponseDTO;
import com.spring.bank.scheduling.service.CreateScheduledPaymentService;
import com.spring.bank.scheduling.service.FindScheduledPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scheduled-payments")
@RequiredArgsConstructor
public class ScheduledPaymentController {

    private final CreateScheduledPaymentService createScheduledPaymentService;
    private final FindScheduledPaymentService findScheduledPaymentService;

    @PostMapping
    public ResponseEntity<ScheduledPaymentResponseDTO> create(@Valid @RequestBody CreateScheduledPaymentDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ScheduledPaymentResponseDTO(createScheduledPaymentService.schedule(dto)));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<ScheduledPaymentResponseDTO>> listByAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(findScheduledPaymentService.listByAccount(accountId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id, @RequestParam Long accountId) {
        findScheduledPaymentService.cancel(id, accountId);
        return ResponseEntity.noContent().build();
    }
}
