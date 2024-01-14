package com.odinbook.notificationservice.config;

import com.odinbook.notificationservice.service.NotificationServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;


import java.util.Map;

@Configuration
public class RedisConfig {

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                            Map< String , MessageListenerAdapter> listenerAdapterMap) {



        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapterMap.get("newPost"), new PatternTopic("newPostChannel"));
        container.addMessageListener(listenerAdapterMap.get("newLike"), new PatternTopic("newLikeChannel"));
        container.addMessageListener(listenerAdapterMap.get("newComment"), new PatternTopic("newCommentChannel"));
        container.addMessageListener(listenerAdapterMap.get("newMessage"), new PatternTopic("newMessageChannel"));


        return container;
    }

    @Bean("newPost")
    MessageListenerAdapter newPostListenerAdapter(NotificationServiceImpl notificationService) {
        return new MessageListenerAdapter(notificationService, "sendNewPostNotification");
    }

    @Bean("newLike")
    MessageListenerAdapter newLikeListenerAdapter(NotificationServiceImpl notificationService) {
        return new MessageListenerAdapter(notificationService, "sendNewLikeNotification");
    }

    @Bean("newComment")
    MessageListenerAdapter newCommentListenerAdapter(NotificationServiceImpl notificationService) {
        return new MessageListenerAdapter(notificationService, "sendNewCommentNotification");
    }

    @Bean("newMessage")
    MessageListenerAdapter newMessageListenerAdapter(NotificationServiceImpl notificationService) {
        return new MessageListenerAdapter(notificationService, "sendNewMessageNotification");
    }


    @Bean
    StringRedisTemplate template(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

}
