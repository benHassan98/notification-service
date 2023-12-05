package com.odinbook.notificationservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;


@Entity
public class AddFriendNotification extends Notification {
    @Column(name = "is_request")
    private Boolean isRequest;

    @Column(name = "is_accepted")
    private Boolean isAccepted;
    @Column(name = "adding_id")
    private Long addingId;

    @Column(name = "added_id")
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

    public Boolean getAccepted() {
        return isAccepted;
    }

    public void setAccepted(Boolean accepted) {
        isAccepted = accepted;
    }
}
