package com.odinbook.notificationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.odinbook.notificationservice.model.*;
import com.odinbook.notificationservice.record.*;
import com.odinbook.notificationservice.repository.NotificationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;


@Service
public class NotificationServiceImpl implements NotificationService{

    @PersistenceContext
    private EntityManager entityManger;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   SimpMessagingTemplate simpMessagingTemplate,
                                   StringRedisTemplate stringRedisTemplate) {
        this.notificationRepository = notificationRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.stringRedisTemplate = stringRedisTemplate;

    }

    @Override
    public Notification createNotification(Notification notification) {
       return notificationRepository.saveAndFlush(notification);
    }

    @Override
    public List<Notification> findNotificationsByReceiverId(Long receiverId) {
        return notificationRepository.findNotificationsByReceiverId(receiverId);
    }



    @Override
    public void sendNewPostNotification(String postJson) {

        NewPostRecord newPostRecord;

        try{
            newPostRecord = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(postJson, NewPostRecord.class);
        }
        catch (JsonProcessingException exception){
            exception.printStackTrace();
            return;
        }

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


    @Override
    public void sendNewCommentNotification(String commentJson){

        NewCommentRecord newCommentRecord;

        try{
            newCommentRecord = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(commentJson, NewCommentRecord.class);
        }
        catch (JsonProcessingException exception){
            exception.printStackTrace();
            return;
        }

        NewCommentNotification notification = new NewCommentNotification();

        notification.setCommentId(newCommentRecord.id());
        notification.setAccountId(newCommentRecord.accountId());
        notification.setReceiverId(newCommentRecord.postAccountId());
        notification.setPostId(newCommentRecord.postId());

        NewCommentNotification savedNotification = (NewCommentNotification) createNotification(notification);
        savedNotification.setType("NewCommentNotification");


        send("/queue/notifications."+notification.getReceiverId(), savedNotification);


    }


    @Override
    public void sendNewLikeNotification(String likeJson){

        NewLikeRecord newLikeRecord;

        try{
            newLikeRecord = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(likeJson, NewLikeRecord.class);
        }
        catch (JsonProcessingException exception){
            exception.printStackTrace();
            return;
        }

        NewLikeNotification notification = new NewLikeNotification();

        notification.setAccountId(newLikeRecord.accountId());
        notification.setPostId(newLikeRecord.postId());
        notification.setReceiverId(newLikeRecord.postAccountId());

        NewLikeNotification savedNotification =  (NewLikeNotification) createNotification(notification);
        savedNotification.setType("NewLikeNotification");

        send("/queue/notifications."+notification.getReceiverId(), savedNotification);

    }


    @Override
    public void sendNewMessageNotification(String messageJson){

        NewMessageRecord newMessageRecord;

        try{
            newMessageRecord = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(messageJson, NewMessageRecord.class);
        }
        catch (JsonProcessingException exception){
            exception.printStackTrace();
            return;
        }

        NewMessageNotification notification = new NewMessageNotification();

        notification.setAccountId(newMessageRecord.senderId());
        notification.setReceiverId(newMessageRecord.receiverId());

        NewMessageNotification savedNotification = (NewMessageNotification) createNotification(notification);
        savedNotification.setType("NewMessageNotification");

        send("/queue/notifications."+notification.getReceiverId(), savedNotification);

    }

    @Override
    public void send(String destination, Notification notification) {

        simpMessagingTemplate.convertAndSend(
                destination,
                notification
        );

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

    @Override
    @Transactional
    public void viewNotificationsByReceiverId(Long receiverId) {

        entityManger
                .createNativeQuery("UPDATE notifications  SET is_viewed = true WHERE receiver_id = :receiverId")
                .setParameter("receiverId",receiverId)
                .executeUpdate();

    }

    @Override
    public void addFriend(AddFriendRecord addFriendRecord) {

        if(this.areFriends(addFriendRecord.addingId(), addFriendRecord.addedId()))
            return;


        AddFriendNotification addFriendNotification = new AddFriendNotification();

        addFriendNotification.setAddingId(addFriendRecord.addingId());
        addFriendNotification.setAddedId(addFriendRecord.addedId());
        addFriendNotification.setRequest(addFriendRecord.isRequest());
        addFriendNotification.setAccepted(addFriendRecord.isAccepted());


        if(addFriendRecord.isRequest()){
            addFriendNotification.setReceiverId(addFriendRecord.addedId());

            AddFriendNotification savedNotification = (AddFriendNotification)
                    this.createNotification(addFriendNotification);

            savedNotification.setType("AddFriendNotification");

            simpMessagingTemplate.convertAndSend(
                    "/queue/notifications."+addFriendRecord.addedId(),
                    savedNotification
            );


        }
        else if (addFriendRecord.isAccepted()){

            addFriendNotification.setReceiverId(addFriendRecord.addingId());

            AddFriendNotification savedNotification = (AddFriendNotification)
                    this.createNotification(addFriendNotification);

            savedNotification.setType("AddFriendNotification");

            simpMessagingTemplate.convertAndSend(
                    "/queue/notifications."+addFriendRecord.addingId(),
                    savedNotification
            );

            String addFriendRecordJson;

            try{
                addFriendRecordJson = new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .writeValueAsString(addFriendRecord);
            }
            catch (JsonProcessingException exception){
                exception.printStackTrace();
                return;
            }

            stringRedisTemplate.convertAndSend("addFriendChannel", addFriendRecordJson);


            this.deleteFriendRequest(
                    addFriendRecord.addingId(),
                    addFriendRecord.addedId()
            );
        }
        else{
            this.deleteFriendRequest(
                    addFriendRecord.addingId(),
                    addFriendRecord.addedId()
            );
        }



    }
}
