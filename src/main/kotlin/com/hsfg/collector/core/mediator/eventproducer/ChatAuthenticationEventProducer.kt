package com.hsfg.collector.core.mediator.eventproducer

import com.hsfg.collector.core.mediator.BotEvent
import com.hsfg.collector.core.mediator.MediatorEventProcessor
import com.hsfg.collector.core.user.application.event.ChatAuthorizedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ChatAuthenticationEventProducer(
    private val mediatorEventProcessor: MediatorEventProcessor,
) {

    @EventListener
    fun onChatAuthorized(event: ChatAuthorizedEvent) {
        mediatorEventProcessor.process(
            event.chatId,
            BotEvent.Authorized()
        )
    }
}