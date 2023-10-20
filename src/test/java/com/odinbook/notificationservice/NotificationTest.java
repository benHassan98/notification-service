package com.odinbook.notificationservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class NotificationTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;
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








    }
}
