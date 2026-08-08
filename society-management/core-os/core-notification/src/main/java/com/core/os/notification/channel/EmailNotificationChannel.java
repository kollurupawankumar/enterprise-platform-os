package com.core.os.notification.channel;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationChannel implements NotificationChannel {

    private final JavaMailSender mailSender;

    public EmailNotificationChannel(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public NotificationChannelType getType() {
        return NotificationChannelType.EMAIL;
    }

    @Override
    public boolean isConfigured() {
        return mailSender != null;
    }

    @Override
    public boolean sendNotification(String title, String message, String recipientEmail) {
        if (!isConfigured() || recipientEmail == null || recipientEmail.isBlank()) {
            System.out.println("📧 [Email Channel Mock]: " + title + " -> " + recipientEmail + " | Content: " + message);
            return true;
        }

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(recipientEmail);
            mailMessage.setSubject(title);
            mailMessage.setText(message);
            mailSender.send(mailMessage);
            return true;
        } catch (Exception ex) {
            System.err.println("Email dispatch failed: " + ex.getMessage());
            return false;
        }
    }
}
