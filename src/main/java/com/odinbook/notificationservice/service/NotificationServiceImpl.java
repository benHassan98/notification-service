package com.odinbook.notificationservice.service;

import com.odinbook.notificationservice.model.Notification;
import com.odinbook.notificationservice.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService{

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Notification createNotification(Notification notification) {
       return notificationRepository.saveAndFlush(notification);
    }

    @Override
    public List<Notification> findNotificationsByReceiverId(Long receiverId) {
        return notificationRepository.findNotificationsByReceiverId(receiverId);
    }
}
