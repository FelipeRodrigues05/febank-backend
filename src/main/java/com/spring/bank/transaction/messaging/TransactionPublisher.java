package com.spring.bank.transaction.messaging;

import com.spring.bank.common.config.messaging.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionPublisher {

    private static final Logger log = LoggerFactory.getLogger(TransactionPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public void publish(Long transactionId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TRANSACTION_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_PROCESS,
                transactionId
        );
        log.debug("Transaction published to exchange: id={}", transactionId);
    }

    public void publishResponse(Long transactionId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TRANSACTION_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_RESPONSE,
                transactionId
        );
        log.debug("Transaction response published: id={}", transactionId);
    }
}
