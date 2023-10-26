package com.odinbook.notificationservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class NewLikeNotification extends Notification{

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "post_id")
    private Long postId;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
}
