package com.odinbook.notificationservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class NewCommentNotification extends Notification{

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "comment_id")
    private Long commentId;

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

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }
}
