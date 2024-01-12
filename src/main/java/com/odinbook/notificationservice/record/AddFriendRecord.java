package com.odinbook.notificationservice.record;


public record AddFriendRecord(Long addingId, Long addedId, Boolean isRequest, Boolean isAccepted) {
}
