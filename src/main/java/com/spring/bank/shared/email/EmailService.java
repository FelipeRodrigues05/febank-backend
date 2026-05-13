package com.spring.bank.shared.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String email, String verificationCode) throws MessagingException {
        String template = loadHtmlTemplate("verification-email.html");
        String html = template.replace("{{ CODE }}", verificationCode);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("nexwallet@suporte.com");
        helper.setTo(email);
        helper.setText(html, true);
        helper.setSubject("Seu código de verificação");

        mailSender.send(message);
    }

    private String loadHtmlTemplate(String templateName) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/" + templateName);
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException error) {
            throw new RuntimeException("Failed to load email template: " + templateName, error);
        }
    }
}
