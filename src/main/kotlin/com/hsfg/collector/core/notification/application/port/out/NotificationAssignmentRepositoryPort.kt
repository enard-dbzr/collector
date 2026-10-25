package com.hsfg.collector.core.notification.application.port.out

import com.hsfg.collector.core.notification.domain.NotificationAssignment
import com.hsfg.collector.core.notification.domain.ToNotifyEventType

interface NotificationAssignmentRepositoryPort {

    fun getByEventType(eventType: ToNotifyEventType): List<NotificationAssignment>
}