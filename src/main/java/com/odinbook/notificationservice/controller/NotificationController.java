package com.odinbook.notificationservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.nimbusds.jose.util.JSONStringUtils;
import com.odinbook.notificationservice.model.Notification;
import com.odinbook.notificationservice.record.AddFriendRecord;
import com.odinbook.notificationservice.record.NewPostRecord;
import com.odinbook.notificationservice.service.NotificationService;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications/{receiverId}")
    public List<Notification> findNotificationsByReceiverId(@PathVariable Long receiverId){

        return  notificationService.findNotificationsByReceiverId(receiverId);
    }

    @GetMapping("/notifications/view/{receiverId}")
    public void viewNotificationsByReceiverId(@PathVariable Long receiverId){
        notificationService.viewNotificationsByReceiverId(receiverId);
    }

    @GetMapping("/checkFriendRequest/{addingId}/{addedId}")
    public Boolean checkFriendRequestInProcess(@PathVariable Long addingId, @PathVariable Long addedId){

        return notificationService.friendRequestInProcess(addingId, addedId) || notificationService.friendRequestInProcess(addedId, addingId);
    }

    @MessageMapping("/addFriend")
    public void addFriend(@Payload AddFriendRecord addFriendRecord){
        notificationService.addFriend(addFriendRecord);
    }

}
