package com.spring.bank.pix.repository;

import com.spring.bank.pix.model.PixQrCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PixQrCodeRepository extends JpaRepository<PixQrCode, String> {
    Optional<PixQrCode> findByIdAndActiveTrue(String id);
    List<PixQrCode> findByAccountIdAndActiveTrue(Long accountId);
}
