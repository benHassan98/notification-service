package com.odinbook.notificationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.odinbook.notificationservice.model.AddFriendNotification;
import com.odinbook.notificationservice.model.Notification;
import com.odinbook.notificationservice.record.*;

import java.util.List;

public interface NotificationService {
    public Notification createNotification(Notification notification);
    public List<Notification> findNotificationsByReceiverId(Long receiverId);
    public void sendNewPostNotification(String postJson);
    public void sendNewCommentNotification(String commentJson);
    public void sendNewLikeNotification(String likeJson);
    public void sendNewMessageNotification(String messageJson);
    public void send(String destination, Notification notification);
    public void deleteFriendRequest(Long addingId, Long addedId);
    public Boolean areFriends(Long addingId, Long addedId);
    public Boolean friendRequestInProcess(Long addingId, Long addedId);
    public void viewNotificationsByReceiverId(Long receiverId);
    public void addFriend(AddFriendRecord addFriendRecord);


}
