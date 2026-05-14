package com.spring.bank.common.config.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    public static final String TRANSACTION_EXCHANGE = "transaction.exchange";
    public static final String TRANSACTION_QUEUE = "transaction.queue";
    public static final String TRANSACTION_RESPONSE_QUEUE = "transaction.response.queue";
    public static final String TRANSACTION_DLQ = "transaction.dlq";

    public static final String TRANSFER_EXCHANGE = "transfer.exchange";
    public static final String TRANSFER_QUEUE = "transfer.queue";
    public static final String TRANSFER_RESPONSE_QUEUE = "transfer.response.queue";
    public static final String TRANSFER_DLQ = "transfer.dlq";

    public static final String ROUTING_KEY_PROCESS = "process";
    public static final String ROUTING_KEY_RESPONSE = "response";

    @Bean
    public DirectExchange transactionExchange() {
        return new DirectExchange(TRANSACTION_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange transferExchange() {
        return new DirectExchange(TRANSFER_EXCHANGE, true, false);
    }

    @Bean
    public Queue transactionQueue() {
        return QueueBuilder.durable(TRANSACTION_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", TRANSACTION_DLQ)
                .build();
    }

    @Bean
    public Queue transactionResponseQueue() {
        return QueueBuilder.durable(TRANSACTION_RESPONSE_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", TRANSACTION_DLQ)
                .build();
    }

    @Bean
    public Queue transactionDlq() {
        return QueueBuilder.durable(TRANSACTION_DLQ).build();
    }

    @Bean
    public Queue transferQueue() {
        return QueueBuilder.durable(TRANSFER_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", TRANSFER_DLQ)
                .build();
    }

    @Bean
    public Queue transferResponseQueue() {
        return QueueBuilder.durable(TRANSFER_RESPONSE_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", TRANSFER_DLQ)
                .build();
    }

    @Bean
    public Queue transferDlq() {
        return QueueBuilder.durable(TRANSFER_DLQ).build();
    }

    @Bean
    public Binding transactionQueueBinding(Queue transactionQueue, DirectExchange transactionExchange) {
        return BindingBuilder.bind(transactionQueue).to(transactionExchange).with(ROUTING_KEY_PROCESS);
    }

    @Bean
    public Binding transactionResponseQueueBinding(Queue transactionResponseQueue, DirectExchange transactionExchange) {
        return BindingBuilder.bind(transactionResponseQueue).to(transactionExchange).with(ROUTING_KEY_RESPONSE);
    }

    @Bean
    public Binding transferQueueBinding(Queue transferQueue, DirectExchange transferExchange) {
        return BindingBuilder.bind(transferQueue).to(transferExchange).with(ROUTING_KEY_PROCESS);
    }

    @Bean
    public Binding transferResponseQueueBinding(Queue transferResponseQueue, DirectExchange transferExchange) {
        return BindingBuilder.bind(transferResponseQueue).to(transferExchange).with(ROUTING_KEY_RESPONSE);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
