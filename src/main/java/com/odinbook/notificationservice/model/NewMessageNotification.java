package com.odinbook.notificationservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;


@Entity
public class NewMessageNotification extends Notification{

    @Column(name = "account_id")
    private Long accountId;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}

