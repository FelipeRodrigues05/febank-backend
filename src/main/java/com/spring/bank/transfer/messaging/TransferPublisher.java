package com.spring.bank.transfer.messaging;

import com.spring.bank.common.config.messaging.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransferPublisher {

    private static final Logger log = LoggerFactory.getLogger(TransferPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public void publish(Long transferId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TRANSFER_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_PROCESS,
                transferId
        );
        log.debug("Transfer published to exchange: id={}", transferId);
    }

    public void publishResponse(Long transferId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TRANSFER_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_RESPONSE,
                transferId
        );
        log.debug("Transfer response published: id={}", transferId);
    }
}
