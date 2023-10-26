package com.odinbook.notificationservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;


@Entity
public class NewMessageNotification extends Notification{

    @Column(name = "sender_id")
    private Long senderId;

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
}

