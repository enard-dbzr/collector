package com.hsfg.collector.core.mediator.eventproducer

import com.hsfg.collector.core.connection.application.event.ConnectionsUpdatedEvent
import com.hsfg.collector.core.mediator.BotEvent
import com.hsfg.collector.core.mediator.MediatorEventProcessor
import com.hsfg.collector.core.mediator.notification.ConnectionsUpdatedNotification
import com.hsfg.collector.core.user.application.serivce.ChatAuthenticationService
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class NotificationEventProducer(
    private val mediatorEventProcessor: MediatorEventProcessor,
    private val chatAuthenticationService: ChatAuthenticationService,
) {

    @EventListener
    fun onConnectionsUpdated(event: ConnectionsUpdatedEvent) {
        chatAuthenticationService.findAuthenticatedChats(event.userId).forEach {
            mediatorEventProcessor.process(
                it,
                BotEvent.NotificationEvent(ConnectionsUpdatedNotification())
            )
        }
    }
}