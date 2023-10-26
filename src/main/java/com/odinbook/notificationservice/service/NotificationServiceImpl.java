package com.odinbook.notificationservice.service;

import com.odinbook.notificationservice.model.*;
import com.odinbook.notificationservice.record.NewCommentRecord;
import com.odinbook.notificationservice.record.NewLikeRecord;
import com.odinbook.notificationservice.record.NewMessageRecord;
import com.odinbook.notificationservice.record.NewPostRecord;
import com.odinbook.notificationservice.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService{

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   SimpMessagingTemplate simpMessagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public Notification createNotification(Notification notification) {
       return notificationRepository.saveAndFlush(notification);
    }

    @Override
    public List<Notification> findNotificationsByReceiverId(Long receiverId) {
        return notificationRepository.findNotificationsByReceiverId(receiverId);
    }


    @ServiceActivator(inputChannel = "newPostChannel")
    @Override
    public void sendNewPostNotification(@Payload NewPostRecord newPostRecord) {

        newPostRecord.notifyAccountList().forEach(account->{

            NewPostNotification notification = new NewPostNotification();

            notification.setAccountId(account);
            notification.setReceiverId(newPostRecord.accountId());
            notification.setPostId(newPostRecord.id());
            notification.setCreated(!newPostRecord.isShared());

            createNotification(notification);

            send(
                    "/queue/notification."+notification.getReceiverId()
                    ,notification);
        });


    }

    @ServiceActivator(inputChannel = "newCommentChannel")
    @Override
    public void sendNewCommentNotification(@Payload NewCommentRecord newCommentRecord) {

        newCommentRecord.notifyAccountList().forEach(account->{

            NewCommentNotification notification = new NewCommentNotification();

            notification.setCommentId(newCommentRecord.id());
            notification.setAccountId(newCommentRecord.accountId());
            notification.setReceiverId(account);
            notification.setPostId(newCommentRecord.postId());

            createNotification(notification);

            send(
                    "/queue/notifications."+notification.getReceiverId(),
                    notification
            );

        });

    }

    @ServiceActivator(inputChannel = "newLikeChannel")
    @Override
    public void sendLikeNotification(@Payload NewLikeRecord newLikeRecord) {

        NewLikeNotification notification = new NewLikeNotification();

        notification.setAccountId(newLikeRecord.accountId());
        notification.setPostId(newLikeRecord.postId());
        notification.setReceiverId(newLikeRecord.postAccountId());

        createNotification(notification);

        send(
                "/queue/notifications."+notification.getReceiverId(),
                notification
        );

    }

    @ServiceActivator(inputChannel = "newMessageChannel")
    @Override
    public void sendMessageNotification(NewMessageRecord newMessageRecord) {

        NewMessageNotification notification = new NewMessageNotification();

        notification.setSenderId(newMessageRecord.senderId());
        notification.setReceiverId(newMessageRecord.receiverId());

        createNotification(notification);

        send(
                "/queue/notifications."+notification.getReceiverId(),
                notification
        );

    }

    @Override
    public void send(String destination, Notification notification) {
        simpMessagingTemplate.convertAndSend(
                destination,
                notification
        );
    }
}
