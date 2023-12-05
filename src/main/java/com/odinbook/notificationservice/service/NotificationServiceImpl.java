package com.odinbook.notificationservice.service;

import com.odinbook.notificationservice.model.*;
import com.odinbook.notificationservice.record.NewCommentRecord;
import com.odinbook.notificationservice.record.NewLikeRecord;
import com.odinbook.notificationservice.record.NewMessageRecord;
import com.odinbook.notificationservice.record.NewPostRecord;
import com.odinbook.notificationservice.repository.NotificationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

@Service
public class NotificationServiceImpl implements NotificationService{

    @PersistenceContext
    private EntityManager entityManger;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RabbitAdmin rabbitAdmin;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   SimpMessagingTemplate simpMessagingTemplate,
                                   RabbitAdmin rabbitAdmin) {
        this.notificationRepository = notificationRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.rabbitAdmin = rabbitAdmin;
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

        newPostRecord.notifyAccountList().forEach(accountId->{

            NewPostNotification notification = new NewPostNotification();


            notification.setAccountId(newPostRecord.accountId());
            notification.setReceiverId(accountId);
            notification.setPostId(newPostRecord.id());
            notification.setCreated(!newPostRecord.isShared());

            NewPostNotification savedNotification = (NewPostNotification) createNotification(notification);
            savedNotification.setType("NewPostNotification");


            send("/queue/notifications."+notification.getReceiverId(), savedNotification);


        });


    }

    @ServiceActivator(inputChannel = "newCommentChannel")
    @Override
    public void sendNewCommentNotification(@Payload NewCommentRecord newCommentRecord) {

        NewCommentNotification notification = new NewCommentNotification();

        notification.setCommentId(newCommentRecord.id());
        notification.setAccountId(newCommentRecord.accountId());
        notification.setReceiverId(newCommentRecord.postAccountId());
        notification.setPostId(newCommentRecord.postId());

        NewCommentNotification savedNotification = (NewCommentNotification) createNotification(notification);
        savedNotification.setType("NewCommentNotification");


        send("/queue/notifications."+notification.getReceiverId(), savedNotification);


    }

    @ServiceActivator(inputChannel = "newLikeChannel")
    @Override
    public void sendLikeNotification(@Payload NewLikeRecord newLikeRecord) {

        NewLikeNotification notification = new NewLikeNotification();

        notification.setAccountId(newLikeRecord.accountId());
        notification.setPostId(newLikeRecord.postId());
        notification.setReceiverId(newLikeRecord.postAccountId());

        NewLikeNotification savedNotification =  (NewLikeNotification) createNotification(notification);
        savedNotification.setType("NewLikeNotification");

        send("/queue/notifications."+notification.getReceiverId(), savedNotification);

    }

    @ServiceActivator(inputChannel = "newMessageChannel")
    @Override
    public void sendMessageNotification(NewMessageRecord newMessageRecord) {

        NewMessageNotification notification = new NewMessageNotification();

        notification.setAccountId(newMessageRecord.senderId());
        notification.setReceiverId(newMessageRecord.receiverId());

        NewMessageNotification savedNotification = (NewMessageNotification) createNotification(notification);
        savedNotification.setType("NewMessageNotification");

        send("/queue/notifications."+notification.getReceiverId(), savedNotification);

    }

    @Override
    public void send(String destination, Notification notification) {
//        TreeMap<String, Object> treeMap = new TreeMap<>();
//        treeMap.put("auto-delete",true);
//        treeMap.put("durable",true);

        if(Objects.nonNull(rabbitAdmin.getQueueInfo(destination.split("/")[2]))){
            System.out.println("dest: "+destination.split("/")[2]);
            System.out.println("Sent: "+notification.getType());

            simpMessagingTemplate.convertAndSend(
                    destination,
                    notification
            );

        }

    }

    @Override
    @Transactional
    public void deleteFriendRequest(Long addingId, Long addedId) {

        entityManger
                .createNativeQuery("DELETE FROM notifications WHERE adding_id = :addingId AND added_id = :addedId")
                .setParameter("addingId",addingId)
                .setParameter("addedId",addedId)
                .executeUpdate();

    }

    @Override
    public Boolean areFriends(Long addingId, Long addedId) {
        return notificationRepository.areFriends(addingId, addedId) == 1;
    }

    @Override
    public Boolean friendRequestInProcess(Long addingId, Long addedId) {
        return notificationRepository.friendRequestInProcess(addingId, addedId) == 1;
    }
}
