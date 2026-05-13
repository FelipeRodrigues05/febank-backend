package com.spring.bank.pix.controller;

import com.spring.bank.pix.dto.CreateScheduledPixDTO;
import com.spring.bank.pix.dto.ScheduledPixResponseDTO;
import com.spring.bank.pix.service.SchedulePixService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pix")
@RequiredArgsConstructor
public class ScheduledPixController {

    private final SchedulePixService schedulePixService;

    @PostMapping("/scheduled")
    public ResponseEntity<ScheduledPixResponseDTO> schedule(@Valid @RequestBody CreateScheduledPixDTO body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ScheduledPixResponseDTO(schedulePixService.schedule(body)));
    }

    @DeleteMapping("/scheduled/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id, @RequestParam Long accountId) {
        schedulePixService.cancel(id, accountId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/scheduled/account/{accountId}")
    public ResponseEntity<List<ScheduledPixResponseDTO>> listByAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(schedulePixService.listByAccount(accountId));
    }
}
