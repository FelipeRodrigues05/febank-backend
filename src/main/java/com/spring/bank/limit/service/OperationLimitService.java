package com.spring.bank.limit.service;

import com.spring.bank.limit.dto.LimitResponseDTO;
import com.spring.bank.limit.dto.UpdateLimitDTO;
import com.spring.bank.limit.enums.LimitType;
import com.spring.bank.limit.model.OperationLimit;
import com.spring.bank.limit.repository.OperationLimitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationLimitService {

    private static final BigDecimal DEFAULT_PIX_DAILY = new BigDecimal("5000.00");
    private static final BigDecimal DEFAULT_TRANSFER_DAILY = new BigDecimal("10000.00");
    private static final BigDecimal DEFAULT_CARD_PURCHASE = new BigDecimal("5000.00");
    private static final BigDecimal DEFAULT_WITHDRAWAL_DAILY = new BigDecimal("2000.00");

    private final OperationLimitRepository operationLimitRepository;

    public BigDecimal getLimit(Long userId, LimitType type) {
        return operationLimitRepository.findByUserIdAndLimitType(userId, type)
                .map(OperationLimit::getMaxAmount)
                .orElseGet(() -> resolveDefaultLimit(type));
    }

    public LimitResponseDTO update(Long userId, UpdateLimitDTO dto) {
        OperationLimit limit = operationLimitRepository
                .findByUserIdAndLimitType(userId, dto.limitType())
                .orElseGet(() -> {
                    OperationLimit newLimit = new OperationLimit();
                    newLimit.setUserId(userId);
                    newLimit.setLimitType(dto.limitType());
                    return newLimit;
                });

        limit.setMaxAmount(dto.maxAmount());
        return new LimitResponseDTO(operationLimitRepository.save(limit));
    }

    public void checkLimit(Long userId, LimitType type, BigDecimal amount) {
        BigDecimal limit = getLimit(userId, type);
        if (amount.compareTo(limit) > 0) {
            throw new IllegalArgumentException(
                    "Amount exceeds your " + type + " limit of R$ " + limit
            );
        }
    }

    public List<LimitResponseDTO> listByUser(Long userId) {
        return Arrays.stream(LimitType.values())
                .map(type -> buildLimitResponse(userId, type))
                .toList();
    }

    private LimitResponseDTO buildLimitResponse(Long userId, LimitType type) {
        return operationLimitRepository.findByUserIdAndLimitType(userId, type)
                .map(LimitResponseDTO::new)
                .orElseGet(() -> new LimitResponseDTO(type, resolveDefaultLimit(type), null));
    }

    private BigDecimal resolveDefaultLimit(LimitType type) {
        return switch (type) {
            case PIX_DAILY -> DEFAULT_PIX_DAILY;
            case TRANSFER_DAILY -> DEFAULT_TRANSFER_DAILY;
            case CARD_PURCHASE_SINGLE -> DEFAULT_CARD_PURCHASE;
            case WITHDRAWAL_DAILY -> DEFAULT_WITHDRAWAL_DAILY;
        };
    }
}
