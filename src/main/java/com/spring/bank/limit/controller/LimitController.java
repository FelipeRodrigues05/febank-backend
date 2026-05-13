package com.spring.bank.limit.controller;

import com.spring.bank.limit.dto.LimitResponseDTO;
import com.spring.bank.limit.dto.UpdateLimitDTO;
import com.spring.bank.limit.service.OperationLimitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/limits")
@RequiredArgsConstructor
public class LimitController {

    private final OperationLimitService limitService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<LimitResponseDTO>> listByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(limitService.listByUser(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<LimitResponseDTO> update(
            @PathVariable Long userId,
            @RequestBody @Valid UpdateLimitDTO body
    ) {
        return ResponseEntity.ok(limitService.update(userId, body));
    }
}
