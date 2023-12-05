package com.odinbook.notificationservice.repository;

import com.odinbook.notificationservice.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    public List<Notification> findNotificationsByReceiverId(Long receiverId);
    @Query(value = "SELECT COUNT(*) FROM friends WHERE adding_id = :addingId AND added_id = :addedId",nativeQuery = true)
    public Long areFriends(@Param("addingId") Long addingId, @Param("addedId") Long addedId);

    @Query(value = "SELECT COUNT(*) FROM notifications WHERE adding_id = :addingId AND added_id = :addedId", nativeQuery = true)
    public Long friendRequestInProcess(@Param("addingId") Long addingId, @Param("addedId") Long addedId);

}
