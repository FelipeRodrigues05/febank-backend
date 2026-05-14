package com.spring.bank.domain.service;

import com.spring.bank.common.exception.PixContactNotFoundException;
import com.spring.bank.domain.dto.pix.CreatePixContactDTO;
import com.spring.bank.domain.dto.pix.PixContactResponseDTO;
import com.spring.bank.domain.dto.pix.UpdatePixContactDTO;
import com.spring.bank.domain.model.Account;
import com.spring.bank.domain.model.PixContact;
import com.spring.bank.domain.repository.PixContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PixContactService {

    private final PixContactRepository pixContactRepository;
    private final AccountService accountService;

    public PixContact add(CreatePixContactDTO data) {
        Account account = accountService.getById(data.accountId());

        PixContact contact = new PixContact();
        contact.setAccount(account);
        contact.setAlias(data.alias());
        contact.setPixKey(data.pixKey());

        return pixContactRepository.save(contact);
    }

    public List<PixContactResponseDTO> listByAccount(Long accountId) {
        return pixContactRepository.findAllByAccountIdOrderByTransferCountDesc(accountId)
                .stream()
                .map(PixContactResponseDTO::new)
                .toList();
    }

    public PixContact updateAlias(Long id, Long accountId, UpdatePixContactDTO data) {
        PixContact contact = pixContactRepository.findByIdAndAccountId(id, accountId)
                .orElseThrow(() -> new PixContactNotFoundException("PIX contact not found: " + id));
        contact.setAlias(data.alias());
        return pixContactRepository.save(contact);
    }

    public void remove(Long id, Long accountId) {
        PixContact contact = pixContactRepository.findByIdAndAccountId(id, accountId)
                .orElseThrow(() -> new PixContactNotFoundException("PIX contact not found: " + id));
        pixContactRepository.delete(contact);
    }

    public void incrementTransferCount(Long accountId, String pixKey) {
        pixContactRepository.findByAccountIdAndPixKey(accountId, pixKey)
                .ifPresent(contact -> {
                    contact.setTransferCount(contact.getTransferCount() + 1);
                    pixContactRepository.save(contact);
                });
    }
}
