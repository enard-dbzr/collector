package com.hsfg.collector.core.mediator.eventproducer

import com.hsfg.collector.core.mediator.BotEvent
import com.hsfg.collector.core.mediator.MediatorEventProcessor
import com.hsfg.collector.core.mediator.notification.ChatNotification
import com.hsfg.collector.core.mediator.notification.ConnectionsUpdatedNotification
import com.hsfg.collector.core.mediator.notification.UserRegisteredNotification
import com.hsfg.collector.core.user.application.serivce.ChatAuthenticationService
import com.hsfg.collector.core.user.domain.UserId
import org.springframework.stereotype.Component

@Component
class ChatNotificationEventProducer(
    private val mediatorEventProcessor: MediatorEventProcessor,
    private val chatAuthenticationService: ChatAuthenticationService,
) {

    fun sendConnectionsUpdated(userId: UserId) {
        send(userId, ConnectionsUpdatedNotification())
    }

    fun sendUserRegistered(usersToSend: List<UserId>, registeredUserId: UserId) {
        usersToSend.forEach { send(it, UserRegisteredNotification(registeredUserId)) }
    }

    private fun send(userId: UserId, notification: ChatNotification) {
        chatAuthenticationService.findAuthenticatedChats(userId).forEach {
            mediatorEventProcessor.process(
                it,
                BotEvent.NotificationEvent(notification)
            )
        }
    }
}