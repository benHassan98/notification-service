package com.odinbook.notificationservice.record;

import java.sql.Date;
import java.util.List;

public record NewPostRecord(Long id,
                            Long accountId,
                            Boolean isShared,
                            List<Long> notifyAccountList) { }
