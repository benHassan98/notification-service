package com.odinbook.notificationservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.odinbook.notificationservice.model.AddFriendNotification;
import com.odinbook.notificationservice.model.NewLikeNotification;
import com.odinbook.notificationservice.model.Notification;
import com.odinbook.notificationservice.repository.NotificationRepository;
import com.odinbook.notificationservice.service.NotificationServiceImpl;
import org.junit.jupiter.api.Test;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class NotificationTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationServiceImpl notificationService;
    @Test
    public void ts() throws JsonProcessingException {

//        String jsonString = new ObjectMapper().writeValueAsString("It WOrks!1!!");
//
//        Message message = new Message(jsonString.getBytes());
//        message.getMessageProperties().setHeader("service","findNotifiedAccountsRequest");
//
//
//        Message receivedMessage = rabbitTemplate.sendAndReceive("odinBook.accountChannel",
//               message
//        );
//
//        System.out.println(receivedMessage);
//        if(receivedMessage != null){
//        System.out.println(Arrays.toString(receivedMessage.getBody()));
//        }


        NewLikeNotification notification = new NewLikeNotification();
        notification.setAccountId(1L);
        notification.setPostId(19L);
        notification.setReceiverId(1L);

        NewLikeNotification notification1 = (NewLikeNotification) notificationService.createNotification(notification);

        System.out.println(notification1.getType());
        System.out.println(notification1.getPostId());

        notificationRepository.deleteById(notification1.getId());






    }
}
