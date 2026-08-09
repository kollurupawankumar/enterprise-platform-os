package com.core.os.notification.channel;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class TeamsNotificationChannel implements NotificationChannel {

    private final RestTemplate restTemplate;

    public TeamsNotificationChannel() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public NotificationChannelType getType() {
        return NotificationChannelType.TEAMS;
    }

    @Override
    public boolean isConfigured() {
        return true;
    }

    @Override
    public boolean sendNotification(String title, String message, String webhookUrl) {
        if (webhookUrl == null || webhookUrl.isBlank() || !webhookUrl.startsWith("http")) {
            System.out.println("👥 [Microsoft Teams Channel Mock]: " + title + " | Content: " + message);
            return true;
        }

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("@type", "MessageCard");
            payload.put("@context", "http://schema.org/extensions");
            payload.put("themeColor", "1F4E79");
            payload.put("summary", title);
            payload.put("title", "🔔 " + title);
            payload.put("text", message);

            restTemplate.postForEntity(webhookUrl, payload, String.class);
            return true;
        } catch (Exception ex) {
            System.err.println("Microsoft Teams Webhook dispatch failed: " + ex.getMessage());
            return false;
        }
    }
}
