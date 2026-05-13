package com.spring.bank.shared.notification;

import com.spring.bank.shared.email.EmailService;
import com.spring.bank.shared.notification.dto.NotificationRequest;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final EmailService emailService;

    public void send(NotificationRequest request) {
        switch (request.channel()) {
            case EMAIL -> sendEmail(request);
            case SMS -> logSms(request);
            case PUSH -> logPush(request);
        }
    }

    private void sendEmail(NotificationRequest request) {
        try {
            emailService.sendVerificationEmail(request.recipient(), request.message());
        } catch (MessagingException error) {
            log.error("Failed to send email to {}: {}", request.recipient(), error.getMessage());
        }
    }

    private void logSms(NotificationRequest request) {
        log.info("SMS → {} | {}", request.recipient(), request.message());
    }

    private void logPush(NotificationRequest request) {
        log.info("PUSH → {} | {}", request.recipient(), request.message());
    }

    public void notifyTransaction(String email, BigDecimal amount, String type) {
        send(new NotificationRequest(
                email,
                "Transaction Alert",
                "A " + type + " of R$ " + amount + " was processed in your account.",
                NotificationChannel.EMAIL
        ));
    }
}
