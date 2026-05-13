package com.spring.bank.pix.service;

import com.spring.bank.pix.model.PixKey;
import com.spring.bank.pix.repository.PixKeyRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeletePixKeyService {

    private static final Logger log = LoggerFactory.getLogger(DeletePixKeyService.class);

    private final PixKeyRepository pixKeyRepository;
    private final FindPixKeyService findPixKeyService;

    public void delete(String pixKeyId) {
        PixKey pixKey = findPixKeyService.getById(pixKeyId);
        pixKeyRepository.delete(pixKey);
        log.info("PIX key deleted: id={}", pixKeyId);
    }
}
