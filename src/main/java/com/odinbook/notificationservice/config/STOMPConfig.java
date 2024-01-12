package com.odinbook.notificationservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class STOMPConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${app.url}")
    private String appUrl;

    private TaskScheduler messageBrokerTaskScheduler;

    @Autowired
    public void setMessageBrokerTaskScheduler(@Lazy TaskScheduler taskScheduler) {
        this.messageBrokerTaskScheduler = taskScheduler;
    }
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        config
                .setApplicationDestinationPrefixes("/app")
                .enableSimpleBroker("/queue")
                .setHeartbeatValue(new long[]{10000, 20000})
                .setTaskScheduler(this.messageBrokerTaskScheduler);

    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket").setAllowedOrigins(appUrl).withSockJS();
    }






}
