package com.odinbook.notificationservice.config;

import com.azure.messaging.webpubsub.WebPubSubServiceClient;
import com.azure.messaging.webpubsub.WebPubSubServiceClientBuilder;
import com.azure.messaging.webpubsub.models.GetClientAccessTokenOptions;
import com.azure.messaging.webpubsub.models.WebPubSubClientAccessToken;
import com.azure.messaging.webpubsub.models.WebPubSubContentType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.odinbook.notificationservice.model.AddFriendNotification;
import com.odinbook.notificationservice.record.AddFriendRecord;
import com.odinbook.notificationservice.service.NotificationService;
import jakarta.annotation.PostConstruct;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class WebPubSubConfig {

    @Value("${spring.cloud.azure.pubsub.connection-string}")
    private String webPubSubConnectStr;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private NotificationService notificationService;

    @PostConstruct
    public void init() throws URISyntaxException {
        WebPubSubServiceClient service = new WebPubSubServiceClientBuilder()
                .connectionString(webPubSubConnectStr)
                .hub("notifications")
                .buildClient();

        WebPubSubClientAccessToken token = service.getClientAccessToken(
                new GetClientAccessTokenOptions()
                        .setUserId("0")
        );

        WebSocketClient webSocketClient = new WebSocketClient(new URI(token.getUrl())){
            @Override
            public void onOpen(ServerHandshake serverHandshake) {

            }

            @Override
            public void onMessage(String jsonString) {
                try {
                    AddFriendRecord addFriendRecord = new ObjectMapper().readValue(
                            jsonString,
                            AddFriendRecord.class);

                    if(addFriendRecord.isRequest()){
                        service.sendToUser(
                                addFriendRecord.addedId().toString(),
                                jsonString,
                                WebPubSubContentType.APPLICATION_JSON
                        );

                    }
                    else{
                        Message message = new Message(jsonString.getBytes());
                        message.getMessageProperties().setHeader("service","addFriendRequest");
                        rabbitTemplate.send("odinBook.accountChannel", message);
                    }

                    AddFriendNotification addFriendNotification = new AddFriendNotification();
                    addFriendNotification.setRequest(addFriendRecord.isRequest());
                    addFriendNotification.setAddingId(addFriendRecord.addingId());
                    addFriendNotification.setAddedId(addFriendRecord.addedId());
                    addFriendNotification.setReceiverId(
                            addFriendRecord.isRequest()?
                                    addFriendRecord.addedId() :
                                    addFriendRecord.addingId()
                    );
                    notificationService.createNotification(addFriendNotification);


                } catch (JsonProcessingException exception) {
                    exception.printStackTrace();
                }

            }

            @Override
            public void onClose(int i, String s, boolean b) {

            }

            @Override
            public void onError(Exception e) {

            }
        };

        webSocketClient.connect();
    }


}
