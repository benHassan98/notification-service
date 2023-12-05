package com.odinbook.notificationservice.controller;

import com.odinbook.notificationservice.model.AddFriendNotification;
import com.odinbook.notificationservice.record.AddFriendRecord;
import com.odinbook.notificationservice.service.NotificationService;
import com.odinbook.notificationservice.service.NotificationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @MessageMapping("/addFriend")
    public void addFriend(@Payload AddFriendRecord addFriendRecord){

        if(notificationService.areFriends(addFriendRecord.addingId(), addFriendRecord.addedId()))
            return;


        AddFriendNotification addFriendNotification = new AddFriendNotification();

        addFriendNotification.setAddingId(addFriendRecord.addingId());
        addFriendNotification.setAddedId(addFriendRecord.addedId());
        addFriendNotification.setRequest(addFriendRecord.isRequest());
        addFriendNotification.setAccepted(addFriendRecord.isAccepted());


        if(addFriendRecord.isRequest()){
            addFriendNotification.setReceiverId(addFriendRecord.addedId());

            AddFriendNotification savedNotification = (AddFriendNotification)
                    notificationService.createNotification(addFriendNotification);

            savedNotification.setType("AddFriendNotification");

            simpMessagingTemplate.convertAndSend(
                    "/queue/notifications."+addFriendRecord.addedId(),
                    savedNotification
            );


        }
        else if (addFriendRecord.isAccepted()){

            addFriendNotification.setReceiverId(addFriendRecord.addingId());

            AddFriendNotification savedNotification = (AddFriendNotification)
                    notificationService.createNotification(addFriendNotification);

            savedNotification.setType("AddFriendNotification");

            simpMessagingTemplate.convertAndSend(
                    "/queue/notifications."+addFriendRecord.addingId(),
                    savedNotification
            );

            simpMessagingTemplate.convertAndSend(
                    "/queue/odinBook.accountChannel",
                    savedNotification);

            notificationService.deleteFriendRequest(
                    addFriendRecord.addingId(),
                    addFriendRecord.addedId()
            );
        }
        else{
            notificationService.deleteFriendRequest(
                    addFriendRecord.addingId(),
                    addFriendRecord.addedId()
            );
        }


    }



}
