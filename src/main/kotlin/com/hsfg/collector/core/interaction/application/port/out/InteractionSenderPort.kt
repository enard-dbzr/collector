package com.hsfg.collector.core.interaction.application.port.out

import com.hsfg.collector.core.interaction.domain.ChatId
import com.hsfg.collector.core.interaction.domain.MessageId
import com.hsfg.collector.core.interaction.domain.content.MessageBody

interface InteractionSenderPort {

    fun sendMessage(chatId: ChatId, body: MessageBody): MessageId
}