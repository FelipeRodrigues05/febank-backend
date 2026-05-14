package com.spring.bank.compliance.service;

import com.spring.bank.compliance.enums.AmlFlagStatus;
import com.spring.bank.compliance.model.AmlFlag;
import com.spring.bank.compliance.repository.AmlFlagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmlServiceTest {

    @Mock
    private AmlFlagRepository amlFlagRepository;

    @InjectMocks
    private AmlService amlService;

    @Test
    void checkAndFlag_shouldFlagWhenAboveThreshold() {
        amlService.checkAndFlag(1L, 10L, new BigDecimal("10000.00"));

        verify(amlFlagRepository, times(1)).save(any());
    }

    @Test
    void checkAndFlag_shouldNotFlagWhenBelowThreshold() {
        amlService.checkAndFlag(1L, 10L, new BigDecimal("9999.99"));

        verify(amlFlagRepository, never()).save(any());
    }

    @Test
    void clearFlag_shouldSetStatusToCleared() {
        AmlFlag flag = new AmlFlag();
        flag.setStatus(AmlFlagStatus.PENDING_REVIEW);

        when(amlFlagRepository.findById(1L)).thenReturn(Optional.of(flag));

        amlService.clearFlag(1L);

        ArgumentCaptor<AmlFlag> captor = ArgumentCaptor.forClass(AmlFlag.class);
        verify(amlFlagRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(AmlFlagStatus.CLEARED);
    }

    @Test
    void reportFlag_shouldSetStatusToReported() {
        AmlFlag flag = new AmlFlag();
        flag.setStatus(AmlFlagStatus.PENDING_REVIEW);

        when(amlFlagRepository.findById(1L)).thenReturn(Optional.of(flag));

        amlService.reportFlag(1L);

        ArgumentCaptor<AmlFlag> captor = ArgumentCaptor.forClass(AmlFlag.class);
        verify(amlFlagRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(AmlFlagStatus.REPORTED);
    }

    @Test
    void clearFlag_shouldThrowWhenNotFound() {
        when(amlFlagRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> amlService.clearFlag(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("AML flag not found");
    }
}
