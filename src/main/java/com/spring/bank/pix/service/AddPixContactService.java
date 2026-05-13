package com.spring.bank.pix.service;

import com.spring.bank.account.model.Account;
import com.spring.bank.account.service.FindAccountService;
import com.spring.bank.pix.dto.CreatePixContactDTO;
import com.spring.bank.pix.model.PixContact;
import com.spring.bank.pix.repository.PixContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddPixContactService {

    private final PixContactRepository pixContactRepository;
    private final FindAccountService findAccountService;

    public PixContact add(CreatePixContactDTO data) {
        Account account = findAccountService.getById(data.accountId());

        PixContact contact = new PixContact();
        contact.setAccount(account);
        contact.setAlias(data.alias());
        contact.setPixKey(data.pixKey());

        return pixContactRepository.save(contact);
    }
}
