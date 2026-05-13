package com.spring.bank.shared.notification.dto;

import com.spring.bank.shared.notification.NotificationChannel;

public record NotificationRequest(
        String recipient,
        String subject,
        String message,
        NotificationChannel channel
) {}
