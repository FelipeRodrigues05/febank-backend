package com.spring.bank.compliance.service;

import com.spring.bank.compliance.enums.KycStatus;
import com.spring.bank.compliance.model.KycRecord;
import com.spring.bank.compliance.repository.KycRepository;
import com.spring.bank.user.model.User;
import com.spring.bank.user.service.FindUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KycServiceTest {

    @Mock
    private KycRepository kycRepository;

    @Mock
    private FindUserService findUserService;

    @InjectMocks
    private KycService kycService;

    @Test
    void submit_shouldCreateApprovedRecordForValidCpf() {
        User user = new User();
        user.setId(1L);

        when(findUserService.getById(1L)).thenReturn(user);
        when(kycRepository.existsByUserId(1L)).thenReturn(false);

        KycRecord savedRecord = new KycRecord();
        savedRecord.setUserId(1L);
        savedRecord.setStatus(KycStatus.APPROVED);
        when(kycRepository.save(any())).thenReturn(savedRecord);

        KycRecord result = kycService.submit(1L, "529.982.247-25");

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(KycStatus.APPROVED);
    }

    @Test
    void submit_shouldThrowWhenAlreadySubmitted() {
        User user = new User();
        user.setId(1L);

        when(findUserService.getById(1L)).thenReturn(user);
        when(kycRepository.existsByUserId(1L)).thenReturn(true);

        assertThatThrownBy(() -> kycService.submit(1L, "529.982.247-25"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("KYC already submitted");
    }

    @Test
    void submit_shouldThrowForInvalidCpf() {
        User user = new User();
        user.setId(1L);

        when(findUserService.getById(1L)).thenReturn(user);
        when(kycRepository.existsByUserId(1L)).thenReturn(false);

        assertThatThrownBy(() -> kycService.submit(1L, "111.111.111-11"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid CPF/CNPJ format");
    }

    @Test
    void submit_shouldThrowForInvalidCnpj() {
        User user = new User();
        user.setId(1L);

        when(findUserService.getById(1L)).thenReturn(user);
        when(kycRepository.existsByUserId(1L)).thenReturn(false);

        assertThatThrownBy(() -> kycService.submit(1L, "11.111.111/0001-11"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid CPF/CNPJ format");
    }

    @Test
    void submit_shouldCreateApprovedRecordForValidCnpj() {
        User user = new User();
        user.setId(1L);

        when(findUserService.getById(1L)).thenReturn(user);
        when(kycRepository.existsByUserId(1L)).thenReturn(false);

        KycRecord savedRecord = new KycRecord();
        savedRecord.setUserId(1L);
        savedRecord.setStatus(KycStatus.APPROVED);
        when(kycRepository.save(any())).thenReturn(savedRecord);

        KycRecord result = kycService.submit(1L, "11.222.333/0001-81");

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(KycStatus.APPROVED);
    }

    @Test
    void getStatus_shouldThrowWhenNotFound() {
        when(kycRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> kycService.getStatus(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("KYC not found");
    }
}
