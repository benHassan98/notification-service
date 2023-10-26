package com.odinbook.notificationservice.controller;

import com.odinbook.notificationservice.model.AddFriendNotification;
import com.odinbook.notificationservice.record.AddFriendRecord;
import com.odinbook.notificationservice.service.NotificationService;
import com.odinbook.notificationservice.service.NotificationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class STOMPController {

    private final NotificationService notificationService;
    private final SimpMessagingTemplate simpMessagingTemplate;


    @Autowired
    public STOMPController(NotificationService notificationService,
                           SimpMessagingTemplate simpMessagingTemplate) {
        this.notificationService = notificationService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("addFriend")
    public void addFriend(@Payload AddFriendRecord addFriendRecord){

        AddFriendNotification addFriendNotification = new AddFriendNotification();

        addFriendNotification.setAddingId(addFriendRecord.addingId());
        addFriendNotification.setAddedId(addFriendRecord.addedId());
        addFriendNotification.setRequest(addFriendRecord.isRequest());

        if(addFriendRecord.isRequest()){
            simpMessagingTemplate.convertAndSend(
                    "/queue/notifications."+addFriendRecord.addedId(),
                    addFriendRecord
            );
        addFriendNotification.setReceiverId(addFriendRecord.addedId());
        }
        else{
            simpMessagingTemplate.convertAndSend(
                    "/queue/notifications."+addFriendRecord.addingId(),
                    addFriendRecord
            );
            addFriendNotification.setReceiverId(addFriendRecord.addingId());
        }

      notificationService.createNotification(addFriendNotification);

    }


}
