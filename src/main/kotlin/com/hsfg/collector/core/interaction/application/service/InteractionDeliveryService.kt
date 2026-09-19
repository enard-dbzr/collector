package com.hsfg.collector.core.interaction.application.service

import com.hsfg.collector.core.interaction.application.dto.outgoing.EditMessageBody
import com.hsfg.collector.core.interaction.application.port.out.InteractionSenderPort
import com.hsfg.collector.core.interaction.domain.ChatId
import com.hsfg.collector.core.interaction.domain.MessageId
import com.hsfg.collector.core.interaction.domain.content.MessageBody
import org.springframework.stereotype.Service

@Service
class InteractionDeliveryService(
    private val sender: InteractionSenderPort
) {

    fun sendMessage(chatId: ChatId, body: MessageBody): MessageId {
        return sender.sendMessage(chatId, body)
    }

    fun editMessage(messageId: MessageId, body: EditMessageBody) {
        sender.editMessage(messageId, body)
    }
}