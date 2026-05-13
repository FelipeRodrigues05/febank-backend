package com.spring.bank.limit.service;

import com.spring.bank.limit.dto.LimitResponseDTO;
import com.spring.bank.limit.dto.UpdateLimitDTO;
import com.spring.bank.limit.enums.LimitType;
import com.spring.bank.limit.model.OperationLimit;
import com.spring.bank.limit.repository.OperationLimitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperationLimitServiceTest {

    @Mock
    private OperationLimitRepository operationLimitRepository;

    @InjectMocks
    private OperationLimitService operationLimitService;

    @Test
    void getLimit_shouldReturnStoredValue() {
        OperationLimit storedLimit = new OperationLimit();
        storedLimit.setUserId(1L);
        storedLimit.setLimitType(LimitType.PIX_DAILY);
        storedLimit.setMaxAmount(new BigDecimal("3000.00"));

        when(operationLimitRepository.findByUserIdAndLimitType(1L, LimitType.PIX_DAILY))
                .thenReturn(Optional.of(storedLimit));

        BigDecimal result = operationLimitService.getLimit(1L, LimitType.PIX_DAILY);

        assertThat(result).isEqualByComparingTo(new BigDecimal("3000.00"));
    }

    @Test
    void getLimit_shouldReturnDefaultWhenNotFound() {
        when(operationLimitRepository.findByUserIdAndLimitType(1L, LimitType.PIX_DAILY))
                .thenReturn(Optional.empty());

        BigDecimal result = operationLimitService.getLimit(1L, LimitType.PIX_DAILY);

        assertThat(result).isEqualByComparingTo(new BigDecimal("5000.00"));
    }

    @Test
    void checkLimit_shouldPassWhenUnderLimit() {
        when(operationLimitRepository.findByUserIdAndLimitType(1L, LimitType.PIX_DAILY))
                .thenReturn(Optional.empty());

        operationLimitService.checkLimit(1L, LimitType.PIX_DAILY, new BigDecimal("4000.00"));
    }

    @Test
    void checkLimit_shouldThrowWhenOverLimit() {
        when(operationLimitRepository.findByUserIdAndLimitType(1L, LimitType.PIX_DAILY))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> operationLimitService.checkLimit(1L, LimitType.PIX_DAILY, new BigDecimal("6000.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("limit");
    }

    @Test
    void update_shouldSaveNewLimit() {
        when(operationLimitRepository.findByUserIdAndLimitType(1L, LimitType.PIX_DAILY))
                .thenReturn(Optional.empty());

        OperationLimit savedLimit = new OperationLimit();
        savedLimit.setUserId(1L);
        savedLimit.setLimitType(LimitType.PIX_DAILY);
        savedLimit.setMaxAmount(new BigDecimal("2000.00"));
        when(operationLimitRepository.save(any())).thenReturn(savedLimit);

        UpdateLimitDTO dto = new UpdateLimitDTO(LimitType.PIX_DAILY, new BigDecimal("2000.00"));
        LimitResponseDTO result = operationLimitService.update(1L, dto);

        assertThat(result).isNotNull();
        assertThat(result.maxAmount()).isEqualByComparingTo(new BigDecimal("2000.00"));
    }
}
