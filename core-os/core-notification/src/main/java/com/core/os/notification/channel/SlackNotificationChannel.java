package com.core.os.notification.channel;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class SlackNotificationChannel implements NotificationChannel {

    private final RestTemplate restTemplate;

    public SlackNotificationChannel() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public NotificationChannelType getType() {
        return NotificationChannelType.SLACK;
    }

    @Override
    public boolean isConfigured() {
        return true;
    }

    @Override
    public boolean sendNotification(String title, String message, String webhookUrl) {
        if (webhookUrl == null || webhookUrl.isBlank() || !webhookUrl.startsWith("http")) {
            System.out.println("💬 [Slack Channel Mock]: " + title + " | Content: " + message);
            return true;
        }

        try {
            Map<String, String> payload = new HashMap<>();
            payload.put("text", "🔔 *" + title + "*\n" + message);

            restTemplate.postForEntity(webhookUrl, payload, String.class);
            return true;
        } catch (Exception ex) {
            System.err.println("Slack Webhook dispatch failed: " + ex.getMessage());
            return false;
        }
    }
}
