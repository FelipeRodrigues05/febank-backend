package com.spring.bank.pix.service;

import com.spring.bank.pix.dto.PixContactResponseDTO;
import com.spring.bank.pix.repository.PixContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindPixContactService {

    private final PixContactRepository pixContactRepository;

    public List<PixContactResponseDTO> listByAccount(Long accountId) {
        return pixContactRepository.findAllByAccountIdOrderByTransferCountDesc(accountId)
                .stream()
                .map(PixContactResponseDTO::new)
                .toList();
    }
}
