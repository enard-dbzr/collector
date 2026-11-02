package com.hsfg.collector.core.notification.application.service

import com.hsfg.collector.core.connection.application.event.ConnectionsUpdatedEvent
import com.hsfg.collector.core.mediator.eventproducer.ChatNotificationEventProducer
import com.hsfg.collector.core.notification.domain.ToNotifyEventType
import com.hsfg.collector.core.user.application.event.UserRegisteredEvent
import org.springframework.context.event.EventListener
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

@Service
class NotificationEventResolver(
    private val notificationAssignmentsService: NotificationAssignmentsService,
    private val notificationProducer: ChatNotificationEventProducer,
) {

    @EventListener
    fun onConnectionsUpdated(event: ConnectionsUpdatedEvent) {
        notificationProducer.sendConnectionsUpdated(event.userId)
    }

    @Order(100)
    @EventListener
    fun onUserRegistered(event: UserRegisteredEvent) {
        notificationProducer.sendUserRegistered(
            notificationAssignmentsService.getUsersToNotify(ToNotifyEventType.USER_REGISTERED),
            event.user.id
        )
    }
}