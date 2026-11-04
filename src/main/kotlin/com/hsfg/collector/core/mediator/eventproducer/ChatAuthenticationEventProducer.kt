package com.hsfg.collector.core.mediator.eventproducer

import com.hsfg.collector.core.mediator.BotEvent
import com.hsfg.collector.core.mediator.MediatorEventProcessor
import com.hsfg.collector.core.user.application.event.ChatAuthorizedEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ChatAuthenticationEventProducer(
    private val mediatorEventProcessor: MediatorEventProcessor,
) {

    companion object {
        val logger = KotlinLogging.logger { }
    }

    @EventListener
    fun onChatAuthorized(event: ChatAuthorizedEvent) {
        try {
            mediatorEventProcessor.process(
                event.chatId,
                BotEvent.Authorized()
            )
        } catch (e: Exception) {
            logger.error(e) { "Failed to process chat authorized event for chat ${event.chatId}" }
        }
    }
}