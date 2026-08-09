package com.core.os.notification.channel;

public interface NotificationChannel {
    NotificationChannelType getType();
    boolean isConfigured();
    boolean sendNotification(String title, String message, String recipientOrWebhook);
}
