package com.odinbook.notificationservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;


@Entity
public class AddFriendNotification extends Notification {
    @Column(name = "is_request")
    private Boolean isRequest;

    @Column(name = "adding_account")
    private Long addingId;


    @Column(name = "added_account")
    private Long addedId;

    public Boolean getRequest() {
        return isRequest;
    }

    public void setRequest(Boolean request) {
        isRequest = request;
    }

    public Long getAddingId() {
        return addingId;
    }

    public void setAddingId(Long addingId) {
        this.addingId = addingId;
    }

    public Long getAddedId() {
        return addedId;
    }

    public void setAddedId(Long addedId) {
        this.addedId = addedId;
    }
}
