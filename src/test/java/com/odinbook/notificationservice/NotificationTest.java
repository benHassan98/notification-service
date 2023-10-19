package com.odinbook.notificationservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;


import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class NotificationTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Test
    public void ts() throws JsonProcessingException {

        String jsonString = new ObjectMapper().writeValueAsString("It WOrks!1!!");

        Message message = new Message(jsonString.getBytes());
        message.getMessageProperties().setHeader("service","findNotifiedAccountsRequest");


        Message receivedMessage = rabbitTemplate.sendAndReceive("odinBook.accountChannel",
               message
        );

        System.out.println(receivedMessage);
        if(receivedMessage != null){
        System.out.println(Arrays.toString(receivedMessage.getBody()));
        }




    }
}
