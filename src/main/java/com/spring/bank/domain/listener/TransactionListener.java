package com.spring.bank.domain.listener;

import com.spring.bank.common.config.messaging.RabbitMQConfig;
import com.spring.bank.domain.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionListener {
    private final TransactionService transactionService;

    @RabbitListener(queues = RabbitMQConfig.TRANSACTION_RESPONSE)
    public void listenResponseQueue(Long transactionId) {
        this.transactionService.completeTransaction(transactionId);
    }
}
