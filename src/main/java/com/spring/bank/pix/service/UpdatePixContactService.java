package com.spring.bank.pix.service;

import com.spring.bank.common.exception.PixContactNotFoundException;
import com.spring.bank.pix.dto.UpdatePixContactDTO;
import com.spring.bank.pix.model.PixContact;
import com.spring.bank.pix.repository.PixContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdatePixContactService {

    private final PixContactRepository pixContactRepository;

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
}
