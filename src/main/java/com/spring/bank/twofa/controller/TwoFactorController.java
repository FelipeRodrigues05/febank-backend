package com.spring.bank.twofa.controller;

import com.spring.bank.twofa.dto.*;
import com.spring.bank.twofa.service.OtpService;
import com.spring.bank.twofa.service.TrustedDeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/2fa")
@RequiredArgsConstructor
public class TwoFactorController {

    private final OtpService otpService;
    private final TrustedDeviceService trustedDeviceService;

    @PostMapping("/otp/send")
    public ResponseEntity<OtpResponseDTO> sendOtp(@RequestBody @Valid SendOtpDTO body) {
        return ResponseEntity.ok(otpService.send(body.userId()));
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<Map<String, Boolean>> verifyOtp(@RequestBody @Valid VerifyOtpDTO body) {
        boolean verified = otpService.verify(body.userId(), body.code());
        return ResponseEntity.ok(Map.of("verified", verified));
    }

    @PostMapping("/devices/trust")
    public ResponseEntity<TrustedDeviceResponseDTO> trustDevice(@RequestBody @Valid TrustDeviceDTO body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trustedDeviceService.trust(body));
    }

    @GetMapping("/devices/{userId}")
    public ResponseEntity<List<TrustedDeviceResponseDTO>> listDevices(@PathVariable Long userId) {
        return ResponseEntity.ok(trustedDeviceService.listDevices(userId));
    }

    @DeleteMapping("/devices/{userId}/{fingerprint}")
    public ResponseEntity<Void> revokeDevice(
            @PathVariable Long userId,
            @PathVariable String fingerprint
    ) {
        trustedDeviceService.revoke(userId, fingerprint);
        return ResponseEntity.noContent().build();
    }
}
