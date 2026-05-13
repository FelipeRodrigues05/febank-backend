package com.spring.bank.savings.service;

import com.spring.bank.common.exception.SavingsBoxNotFoundException;
import com.spring.bank.savings.model.SavingsBox;
import com.spring.bank.savings.repository.SavingsBoxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindSavingsBoxService {

    private final SavingsBoxRepository savingsBoxRepository;

    public SavingsBox getById(Long id) {
        return savingsBoxRepository.findById(id).orElseThrow(() ->
                new SavingsBoxNotFoundException(String.format("Savings box with ID %s not found", id))
        );
    }

    public List<SavingsBox> listByAccount(Long accountId) {
        return savingsBoxRepository.findAllByAccountId(accountId);
    }
}
