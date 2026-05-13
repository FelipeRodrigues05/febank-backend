package com.spring.bank.pix.service;

import com.spring.bank.pix.repository.PixContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncrementPixContactService {

    private final PixContactRepository pixContactRepository;

    public void increment(Long accountId, String pixKey) {
        pixContactRepository.findByAccountIdAndPixKey(accountId, pixKey)
                .ifPresent(contact -> {
                    contact.setTransferCount(contact.getTransferCount() + 1);
                    pixContactRepository.save(contact);
                });
    }
}
