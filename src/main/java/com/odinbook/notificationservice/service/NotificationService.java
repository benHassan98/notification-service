package com.odinbook.notificationservice.service;

import com.odinbook.notificationservice.model.Notification;

import java.util.List;

public interface NotificationService {
    public Notification createNotification(Notification notification);
    public List<Notification> findNotificationsByReceiverId(Long receiverId);
}
