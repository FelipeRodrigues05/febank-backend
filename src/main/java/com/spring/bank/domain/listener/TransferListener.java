package com.spring.bank.domain.listener;

import com.spring.bank.common.config.messaging.RabbitMQConfig;
import com.spring.bank.domain.dto.transfer.CreateTransferDTO;
import com.spring.bank.domain.model.Transfer;
import com.spring.bank.domain.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferListener {
    private final TransferService transferService;

    @RabbitListener(queues = RabbitMQConfig.TRANSFER_RESPONSE)
    public void listenResponseQueue(Long transferId) throws Exception {
        this.transferService.completeTransfer(transferId);
    }
}
