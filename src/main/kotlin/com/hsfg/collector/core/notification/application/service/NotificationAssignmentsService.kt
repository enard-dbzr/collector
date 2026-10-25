package com.hsfg.collector.core.notification.application.service

import com.hsfg.collector.core.notification.application.port.out.NotificationAssignmentRepositoryPort
import com.hsfg.collector.core.notification.domain.ToNotifyEventType
import com.hsfg.collector.core.user.domain.UserId
import org.springframework.stereotype.Service

@Service
class NotificationAssignmentsService(
    private val repository: NotificationAssignmentRepositoryPort,
) {

    fun getUsersToNotify(eventType: ToNotifyEventType): List<UserId> {
        return repository.getByEventType(eventType).map { it.userId }
    }
}