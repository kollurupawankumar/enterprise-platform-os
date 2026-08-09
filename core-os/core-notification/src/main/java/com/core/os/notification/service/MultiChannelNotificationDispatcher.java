package com.core.os.notification.service;

import com.core.os.notification.channel.NotificationChannel;
import com.core.os.notification.channel.NotificationChannelType;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class MultiChannelNotificationDispatcher {

    private final Map<NotificationChannelType, NotificationChannel> channels = new EnumMap<>(NotificationChannelType.class);

    public MultiChannelNotificationDispatcher(List<NotificationChannel> channelList) {
        for (NotificationChannel channel : channelList) {
            channels.put(channel.getType(), channel);
        }
    }

    public boolean dispatch(NotificationChannelType channelType, String title, String message, String recipientOrWebhook) {
        NotificationChannel channel = channels.get(channelType);
        if (channel != null) {
            return channel.sendNotification(title, message, recipientOrWebhook);
        }
        System.err.println("Notification channel " + channelType + " not supported or registered.");
        return false;
    }

    public void dispatchMultiChannel(List<NotificationChannelType> channelTypes, String title, String message, String recipientOrWebhook) {
        for (NotificationChannelType type : channelTypes) {
            dispatch(type, title, message, recipientOrWebhook);
        }
    }
}
