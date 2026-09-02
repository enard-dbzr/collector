package com.hsfg.collector.core.interaction.application.port.out

import com.hsfg.collector.core.interaction.application.dto.incoming.IncomingEvent
import com.hsfg.collector.core.interaction.domain.ChatId

interface IncomingEventProcessor {
    fun process(chatId: ChatId, event: IncomingEvent)
}