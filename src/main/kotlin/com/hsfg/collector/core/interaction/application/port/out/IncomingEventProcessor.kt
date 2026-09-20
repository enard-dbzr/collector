package com.hsfg.collector.core.interaction.application.port.out

import com.hsfg.collector.core.interaction.domain.ChatId
import com.hsfg.collector.core.mediator.BotEvent

interface IncomingEventProcessor {
    fun process(chatId: ChatId, event: BotEvent)
}