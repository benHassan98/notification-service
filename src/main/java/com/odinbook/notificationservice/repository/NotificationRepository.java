package com.odinbook.notificationservice.repository;

import com.odinbook.notificationservice.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    public List<Notification> findNotificationsByReceiverId(Long receiverId);
}
