package com.spring.bank.common.config.messaging;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    public static final String TRANSACTION_QUEUE = "transaction_queue";
    public static final String TRANSACTION_RESPONSE = "transaction_response";

    public static final String TRANSFER_QUEUE = "transfer_queue";
    public static final String TRANSFER_RESPONSE = "transfer_response";

    @Bean
    public Queue transactionQueue() {
        return new Queue(TRANSACTION_QUEUE, true);
    }

    @Bean
    public Queue transactionResponse() {
        return new Queue(TRANSACTION_RESPONSE, true);
    }

    @Bean
    public Queue transferQueue() {
        return new Queue(TRANSFER_QUEUE, true);
    }

    @Bean
    public Queue transferResponse() {
        return new Queue(TRANSFER_RESPONSE, true);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
