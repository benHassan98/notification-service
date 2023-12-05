package com.odinbook.notificationservice.service;

import com.odinbook.notificationservice.model.AddFriendNotification;
import com.odinbook.notificationservice.model.Notification;
import com.odinbook.notificationservice.record.NewCommentRecord;
import com.odinbook.notificationservice.record.NewLikeRecord;
import com.odinbook.notificationservice.record.NewMessageRecord;
import com.odinbook.notificationservice.record.NewPostRecord;

import java.util.List;

public interface NotificationService {
    public Notification createNotification(Notification notification);
    public List<Notification> findNotificationsByReceiverId(Long receiverId);
    public void sendNewPostNotification(NewPostRecord newPostRecord);
    public void sendNewCommentNotification(NewCommentRecord newCommentRecord);
    public void sendLikeNotification(NewLikeRecord newLikeRecord);
    public void sendMessageNotification(NewMessageRecord newMessageRecord);
    public void send(String destination, Notification notification);
    public void deleteFriendRequest(Long addingId, Long addedId);
    public Boolean areFriends(Long addingId, Long addedId);
    public Boolean friendRequestInProcess(Long addingId, Long addedId);

}
