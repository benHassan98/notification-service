package com.odinbook.notificationservice.config;

import com.odinbook.notificationservice.record.NewCommentRecord;
import com.odinbook.notificationservice.record.NewLikeRecord;
import com.odinbook.notificationservice.record.NewMessageRecord;
import com.odinbook.notificationservice.record.NewPostRecord;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.inbound.AmqpInboundChannelAdapter;
import org.springframework.integration.amqp.outbound.AmqpOutboundEndpoint;
import org.springframework.integration.annotation.Filter;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSelector;
import org.springframework.integration.json.JsonToObjectTransformer;
import org.springframework.integration.router.HeaderValueRouter;
import org.springframework.integration.transformer.HeaderEnricher;
import org.springframework.integration.transformer.support.StaticHeaderValueMessageProcessor;
import org.springframework.messaging.MessageChannel;

import java.util.Collections;

@Configuration
public class IntegrationConfig {
    @Bean
    public AbstractMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer messageListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
        messageListenerContainer.setQueueNames("odinBook.notificationChannel");
        return messageListenerContainer;
    }

    @Bean
    public AmqpInboundChannelAdapter inboundChannelAdapter(AbstractMessageListenerContainer messageListenerContainer) {
        AmqpInboundChannelAdapter adapter = new AmqpInboundChannelAdapter(messageListenerContainer);
        adapter.setOutputChannelName("fromRabbit");
        return adapter;
    }

    @Bean
    public MessageChannel fromRabbit() {
        return new DirectChannel();
    }


    @Bean
    @Filter(
            inputChannel = "fromRabbit",
            outputChannel = "toAccountChannel",
            discardChannel = "routerChannel")
    public MessageSelector serviceFilter() {
        return message -> !message.getHeaders().containsKey("withNotifiedAccounts")
                &&
                "newPost".equals(message.getHeaders().get("notificationType"));
    }


    @Bean
    public MessageChannel routerChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "routerChannel")
    public HeaderValueRouter headerValueRouter() {
        HeaderValueRouter router = new HeaderValueRouter("notificationType");
        router.setChannelMapping("newPost", "newPostTransformerChannel");
        router.setChannelMapping("newComment", "newCommentTransformerChannel");
        router.setChannelMapping("newLike", "newLikeTransformerChannel");
        router.setChannelMapping("newMessage", "newMessageTransformerChannel");
        return router;
    }

    @Bean
    public MessageChannel newPostTransformerChannel() {
        return new DirectChannel();
    }
    @Bean
    public MessageChannel newCommentTransformerChannel() {
        return new DirectChannel();
    }
    @Bean
    public MessageChannel newLikeTransformerChannel() {
        return new DirectChannel();
    }
    @Bean
    public MessageChannel newMessageTransformerChannel() {
        return new DirectChannel();
    }

    @Bean
    @Transformer(
            inputChannel = "newPostTransformerChannel",
            outputChannel = "newPostChannel")
    public JsonToObjectTransformer newPostTransformer() {
        return new JsonToObjectTransformer(NewPostRecord.class);
    }

    @Bean
    @Transformer(
            inputChannel = "newCommentTransformerChannel",
            outputChannel = "newCommentChannel")
    public JsonToObjectTransformer newCommentTransformer() {
        return new JsonToObjectTransformer(NewCommentRecord.class);
    }
    @Bean
    @Transformer(
            inputChannel = "newLikeTransformerChannel",
            outputChannel = "newLikeChannel")
    public JsonToObjectTransformer newLikeTransformer() {
        return new JsonToObjectTransformer(NewLikeRecord.class);
    }

    @Bean
    @Transformer(
            inputChannel = "newMessageTransformerChannel",
            outputChannel = "newMessageChannel")
    public JsonToObjectTransformer newMessageTransformer() {
        return new JsonToObjectTransformer(NewMessageRecord.class);
    }


    @Bean
    public MessageChannel newPostChannel() {
        return new DirectChannel();
    }
    @Bean
    public MessageChannel newCommentChannel() {
        return new DirectChannel();
    }
    @Bean
    public MessageChannel newLikeChannel() {
        return new DirectChannel();
    }
    @Bean
    public MessageChannel newMessageChannel() {
        return new DirectChannel();
    }
    @Bean
    public MessageChannel toAccountChannel() {
        return new DirectChannel();
    }

    @Bean
    @Transformer(inputChannel = "toAccountChannel", outputChannel = "accountChannel")
    public HeaderEnricher headerEnricherService() {
        return new HeaderEnricher(Collections.singletonMap(
                "withNotifiedAccounts", new StaticHeaderValueMessageProcessor<>(true)));
    }

    @Bean
    public MessageChannel accountChannel() {
        return new DirectChannel();
    }

    @ServiceActivator(inputChannel = "accountChannel")
    @Bean
    public AmqpOutboundEndpoint amqpOutboundEndpoint(AmqpTemplate amqpTemplate) {
        AmqpOutboundEndpoint adapter = new AmqpOutboundEndpoint(amqpTemplate);
        adapter.setRoutingKey("odinBook.accountChannel");
        return adapter;
    }


}
