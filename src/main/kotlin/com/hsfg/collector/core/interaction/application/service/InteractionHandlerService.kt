package com.hsfg.collector.core.interaction.application.service

import com.hsfg.collector.core.interaction.application.dto.incoming.IncomingEvent
import com.hsfg.collector.core.interaction.application.port.out.IncomingEventProcessor
import com.hsfg.collector.core.interaction.domain.ChatId
import org.springframework.stereotype.Service

@Service
class InteractionHandlerService(
    private val eventProcessor: IncomingEventProcessor
) {

    fun handle(chatId: ChatId, event: IncomingEvent) {
        eventProcessor.process(chatId, event)
    }
}