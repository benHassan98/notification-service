package com.odinbook.notificationservice.record;



public record NewCommentRecord(Long id,
                               Long postId,
                               Long accountId,
                               Long postAccountId) { }
