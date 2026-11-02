package com.hsfg.collector.core.notification.domain

import com.hsfg.collector.core.user.domain.UserId

data class NotificationAssignment(val userId: UserId, val eventType: ToNotifyEventType)
