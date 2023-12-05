package com.odinbook.notificationservice.controller;

import com.odinbook.notificationservice.model.Notification;
import com.odinbook.notificationservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NotificationsController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationsController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications/{receiverId}")
    public List<Notification> findNotificationsByReceiverId(@PathVariable Long receiverId){

        return  notificationService.findNotificationsByReceiverId(receiverId);
    }
    @GetMapping("/checkFriendRequest/{addingId}/{addedId}")
    public Boolean checkFriendRequestInProcess(@PathVariable Long addingId, @PathVariable Long addedId){

        return notificationService.friendRequestInProcess(addingId, addedId) || notificationService.friendRequestInProcess(addedId, addingId);
    }

}
