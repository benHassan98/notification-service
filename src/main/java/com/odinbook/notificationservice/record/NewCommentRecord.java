package com.odinbook.notificationservice.record;

import java.sql.Date;
import java.util.List;

public record NewCommentRecord(Long id,
                               Long postId,
                               Long accountId,
                               List<Long> notifyAccountList) { }
