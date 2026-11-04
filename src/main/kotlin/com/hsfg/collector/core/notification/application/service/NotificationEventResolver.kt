package com.hsfg.collector.core.notification.application.service

import com.hsfg.collector.core.connection.application.event.ConnectionsUpdatedEvent
import com.hsfg.collector.core.mediator.eventproducer.ChatNotificationEventProducer
import com.hsfg.collector.core.notification.domain.ToNotifyEventType
import com.hsfg.collector.core.user.application.event.UserRegisteredEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

@Service
class NotificationEventResolver(
    private val notificationAssignmentsService: NotificationAssignmentsService,
    private val notificationProducer: ChatNotificationEventProducer,
) {

    companion object {
        val logger = KotlinLogging.logger {}
    }

    @EventListener
    fun onConnectionsUpdated(event: ConnectionsUpdatedEvent) {
        try {
            notificationProducer.sendConnectionsUpdated(event.userId)
        } catch (e: Exception) {
            logger.error(e) { "Failed to send connections updated notification for user ${event.userId}" }
        }
    }

    @Order(100)
    @EventListener
    fun onUserRegistered(event: UserRegisteredEvent) {
        try {
            notificationProducer.sendUserRegistered(
                notificationAssignmentsService.getUsersToNotify(ToNotifyEventType.USER_REGISTERED),
                event.user.id
            )
        } catch (e: Exception) {
            logger.error(e) { "Failed to send user registered notification for user ${event.user.id}" }
        }
    }
}