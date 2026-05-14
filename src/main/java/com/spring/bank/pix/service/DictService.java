package com.spring.bank.pix.service;

import com.spring.bank.account.model.Account;
import com.spring.bank.pix.dto.DictQueryResponseDTO;
import com.spring.bank.pix.model.PixKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DictService {

    private static final String INSTITUTION_NAME = "NexWallet Bank";

    private final FindPixKeyService findPixKeyService;

    public DictQueryResponseDTO query(String pixKey) {
        PixKey foundKey = findPixKeyService.getByKey(pixKey);
        Account account = foundKey.getAccount();

        return new DictQueryResponseDTO(
                foundKey.getKey(),
                foundKey.getType().name(),
                account.getUser().getName(),
                INSTITUTION_NAME,
                account.getId(),
                account.getNumber()
        );
    }
}
