package com.spring.bank.pix.service;

import com.spring.bank.common.exception.PixKeyNotFoundException;
import com.spring.bank.pix.model.PixKey;
import com.spring.bank.pix.repository.PixKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindPixKeyService {

    private final PixKeyRepository pixKeyRepository;

    public PixKey getById(String id) {
        return pixKeyRepository.findById(id).orElseThrow(() ->
                new PixKeyNotFoundException("PIX key with ID " + id + " not found"));
    }

    public PixKey getByKey(String key) {
        return pixKeyRepository.findByKey(key).orElseThrow(() ->
                new PixKeyNotFoundException("PIX key not found: " + key));
    }

    public List<PixKey> listByAccount(Long accountId) {
        return pixKeyRepository.findAllByAccountId(accountId);
    }
}
